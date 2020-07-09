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
package com.admin.system.mnt.service.impl;

import cn.hutool.core.util.IdUtil;
import com.admin.modules.mnt.service.dto.DeployHistoryDto;
import com.admin.modules.mnt.service.dto.DeployHistoryQueryCriteria;
import com.admin.system.mnt.mapper.IDeployHistoryMapper;
import com.admin.system.mnt.model.DatabaseModel;
import com.admin.system.mnt.model.DeployHistoryModel;
import com.admin.system.mnt.service.IDeployHistoryService;
import com.admin.utils.DozerUtils;
import com.admin.utils.FileUtil;
import com.admin.utils.PageUtil;
import com.admin.utils.ValidationUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
@RequiredArgsConstructor
@Service
public class AdminDeployHistoryServiceImpl extends ServiceImpl<IDeployHistoryMapper, DeployHistoryModel>
        implements IDeployHistoryService {

    private final Mapper mapper;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(DeployHistoryQueryCriteria criteria, IPage pageable) {
        IPage<DatabaseModel> page = this.page(pageable, this.buildWrapper(criteria));
        List<DeployHistoryDto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), DeployHistoryDto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    @Override
    public List<DeployHistoryDto> queryAll(DeployHistoryQueryCriteria criteria) {
        List<DeployHistoryModel> users = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, users, DeployHistoryDto.class);
    }

    private QueryWrapper<DeployHistoryModel> buildWrapper(DeployHistoryQueryCriteria criteria) {
        QueryWrapper<DeployHistoryModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getDeployDate())
                    && criteria.getDeployDate().size() >= 2;
            Timestamp start = haveTime ? criteria.getDeployDate().get(0) : null;
            Timestamp end = haveTime ? criteria.getDeployDate().get(1) : null;
            query = new QueryWrapper<DeployHistoryModel>();
            query.lambda()
                    .nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                            i -> i.like(DeployHistoryModel::getAppName, criteria.getBlurry()).or()
                                    .like(DeployHistoryModel::getIp, criteria.getBlurry()).or()
                                    .like(DeployHistoryModel::getDeployUser, criteria.getBlurry()))
                    .eq(null != criteria.getDeployId(), DeployHistoryModel::getDeployId, criteria.getDeployId())
                    .between(haveTime, DeployHistoryModel::getDeployDate, start, end);
        }
        return query;
    }

    @Override
    public DeployHistoryDto findById(String id) {
        DeployHistoryModel deployhistory = Optional.ofNullable(this.getById(id)).orElseGet(DeployHistoryModel::new);
        ValidationUtil.isNull(deployhistory.getId(), "DeployHistory", "id", id);
        return mapper.map(deployhistory, DeployHistoryDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeployHistoryModel resources) {
        resources.setId(IdUtil.simpleUUID());
        this.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void download(List<DeployHistoryDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeployHistoryDto deployHistoryDto : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部署编号", deployHistoryDto.getDeployId());
            map.put("应用名称", deployHistoryDto.getAppName());
            map.put("部署IP", deployHistoryDto.getIp());
            map.put("部署时间", deployHistoryDto.getDeployDate());
            map.put("部署人员", deployHistoryDto.getDeployUser());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}
