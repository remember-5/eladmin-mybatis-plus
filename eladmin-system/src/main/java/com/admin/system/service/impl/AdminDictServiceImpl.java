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

import com.admin.modules.system.service.dto.DictDetailDto;
import com.admin.modules.system.service.dto.DictDto;
import com.admin.modules.system.service.dto.DictQueryCriteria;
import com.admin.system.mapper.IDictMapper;
import com.admin.system.model.DictModel;
import com.admin.system.service.IDictService;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "dict")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminDictServiceImpl extends ServiceImpl<IDictMapper, DictModel> implements IDictService {
    private final Mapper mapper;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @Cacheable
    public Map<String, Object> queryAll(DictQueryCriteria criteria, IPage pageable) {
        IPage<DictModel> page = this.page(pageable, buildWrapper(criteria));
        return PageUtil.toPage(page.getRecords(), page.getTotal());
    }

    @Override
    public List<DictDto> queryAll(DictQueryCriteria criteria) {
        List<DictModel> list = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, list, DictDto.class);
    }

    private QueryWrapper<DictModel> buildWrapper(DictQueryCriteria criteria) {
        QueryWrapper<DictModel> query = null;
        if (null != criteria) {
            query = new QueryWrapper<DictModel>();
            query.lambda().nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                    i -> i.like(DictModel::getName, criteria.getBlurry()).like(DictModel::getDescription,
                            criteria.getBlurry()));
        }
        return query;
    }

    @Override
    @Cacheable(key = "#p0")
    public DictDto findById(Long id) {
        DictModel dict = Optional.ofNullable(this.getById(id)).orElseGet(DictModel::new);
        ValidationUtil.isNull(dict.getId(), "Dict", "id", id);
        return mapper.map(dict, DictDto.class);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void create(DictModel resources) {
        this.save(resources);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(DictModel resources) {
        DictModel dict = Optional.ofNullable(this.getById(resources.getId())).orElseGet(DictModel::new);
        ValidationUtil.isNull(dict.getId(), "Dict", "id", resources.getId());
        resources.setId(dict.getId());
        this.saveOrUpdate(resources);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void download(List<DictDto> dictDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictDto dictDTO : dictDtos) {
            if (CollectionUtils.isNotEmpty(dictDTO.getDictDetails())) {
                for (DictDetailDto dictDetail : dictDTO.getDictDetails()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dictDTO.getName());
                    map.put("字典描述", dictDTO.getDescription());
                    map.put("字典标签", dictDetail.getLabel());
                    map.put("字典值", dictDetail.getValue());
                    map.put("创建日期", dictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("字典名称", dictDTO.getName());
                map.put("字典描述", dictDTO.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dictDTO.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }
}
