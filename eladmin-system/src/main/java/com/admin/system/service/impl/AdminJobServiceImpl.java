/*
 *  Copyright 2019-2020 Fang Jin Biao
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.admin.system.service.impl;

import com.admin.exception.BadRequestException;
import com.admin.exception.EntityExistException;
import com.admin.modules.system.service.dto.JobDto;
import com.admin.modules.system.service.dto.JobQueryCriteria;
import com.admin.system.mapper.IJobMapper;
import com.admin.system.mapper.IUserMapper;
import com.admin.system.model.JobModel;
import com.admin.system.service.IJobService;
import com.admin.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月1日
 */
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "job")
public class AdminJobServiceImpl extends ServiceImpl<IJobMapper, JobModel> implements IJobService {

    private final Mapper mapper;

    private final IUserMapper userMapper;

    private final RedisUtils redisUtils;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, Object> queryAll(JobQueryCriteria criteria, IPage pageable) {
        IPage<JobModel> page = this.page(pageable, buildWrapper(criteria));
        List<JobDto> jobs = DozerUtils.mapList(mapper, page.getRecords(), JobDto.class);
        return PageUtil.toPage(jobs, page.getTotal());
    }

    @Override
    public List<JobDto> queryAll(JobQueryCriteria criteria) {
        List<JobModel> list = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, list, JobDto.class);
    }

    @Override
    public List<JobDto> queryAll() {
        List<JobModel> list = this.list();
        return DozerUtils.mapList(mapper, list, JobDto.class);
    }

    private QueryWrapper<JobModel> buildWrapper(JobQueryCriteria criteria) {
        QueryWrapper<JobModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<JobModel>();
            query.lambda().like(StringUtils.isNotEmpty(criteria.getName()), JobModel::getName, criteria.getName())
                    .eq(null != criteria.getEnabled(), JobModel::getEnabled, criteria.getEnabled())
                    .between(haveTime, JobModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    public JobDto findById(Long id) {
        JobModel job = Optional.ofNullable(this.getById(id)).orElseGet(JobModel::new);
        ValidationUtil.isNull(job.getId(), "Job", "id", id);
        return mapper.map(job, JobDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(JobModel resources) {
        QueryWrapper<JobModel> query = new QueryWrapper<JobModel>();
        query.lambda().eq(JobModel::getName, resources.getName());
        JobModel job = this.getOne(query);
        if (job != null) {
            throw new EntityExistException(JobModel.class, "name", resources.getName());
        }
        this.save(resources);
    }

    @Override
    @CacheEvict(key = "'id:' + #p0.id")
    @Transactional(rollbackFor = Exception.class)
    public void update(JobModel resources) {
        JobModel job = Optional.ofNullable(this.getById(resources.getId())).orElseGet(JobModel::new);
        QueryWrapper<JobModel> query = new QueryWrapper<JobModel>();
        query.lambda().eq(JobModel::getName, resources.getName());
        JobModel old = this.getOne(query);
        if (old != null && !old.getId().equals(resources.getId())) {
            throw new EntityExistException(JobModel.class, "name", resources.getName());
        }
        ValidationUtil.isNull(job.getId(), "Job", "id", resources.getId());
        resources.setId(job.getId());
        this.saveOrUpdate(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        this.removeByIds(ids);
        // 删除缓存
        redisUtils.delByKeys("job::id:", ids);
    }

    @Override
    public void download(List<JobDto> jobDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>(jobDtos.size());
        for (JobDto jobDTO : jobDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("岗位名称", jobDTO.getName());
            map.put("岗位状态", jobDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", jobDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userMapper.countByJobs(ids) > 0) {
            throw new BadRequestException("所选的岗位中存在用户关联，请解除关联再试！");
        }
    }
}
