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
import com.admin.modules.system.service.dto.DictDetailQueryCriteria;
import com.admin.system.mapper.IDictDetailMapper;
import com.admin.system.mapper.IDictMapper;
import com.admin.system.model.DictDetailModel;
import com.admin.system.model.DictModel;
import com.admin.system.service.IDictDetailService;
import com.admin.utils.DozerUtils;
import com.admin.utils.PageUtil;
import com.admin.utils.RedisUtils;
import com.admin.utils.ValidationUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "dict")
public class AdminDictDetailServiceImpl extends ServiceImpl<IDictDetailMapper, DictDetailModel>
        implements IDictDetailService {
    private final Mapper mapper;

    private final IDictMapper dictMapper;

    private final RedisUtils redisUtils;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Map<String, Object> queryAll(DictDetailQueryCriteria criteria, IPage pageable) {
        List<DictDetailModel> list = this.baseMapper.selectJoin(pageable, criteria);
        return PageUtil.toPage(DozerUtils.mapList(mapper, list, DictDetailDto.class), pageable.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictDetailModel resources) {
        this.save(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictDetailModel resources) {
        DictDetailModel dictDetail = Optional.ofNullable(this.getById(resources.getId()))
                .orElseGet(DictDetailModel::new);
        ValidationUtil.isNull(dictDetail.getId(), "DictDetail", "id", resources.getId());
        resources.setId(dictDetail.getId());
        this.saveOrUpdate(resources);
        // 清理缓存
        delCaches(resources);
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<DictDetailDto> getDictByName(String name) {
        List<DictDetailModel> list = this.baseMapper.findByDictName(name);
        return DozerUtils.mapList(mapper, list, DictDetailDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DictDetailModel dictDetail = Optional.ofNullable(this.getById(id)).orElseGet(DictDetailModel::new);
        // 清理缓存
        delCaches(dictDetail);
        this.removeById(id);
    }

    public void delCaches(DictDetailModel dictDetail) {
        DictModel dict = Optional.ofNullable(dictMapper.selectById(dictDetail.getDict().getId()))
                .orElseGet(DictModel::new);
        redisUtils.del("dept::name:" + dict.getName());
    }
}
