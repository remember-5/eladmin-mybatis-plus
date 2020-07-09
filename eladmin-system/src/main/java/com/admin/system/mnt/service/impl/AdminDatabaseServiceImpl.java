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
import com.admin.modules.mnt.service.dto.DatabaseDto;
import com.admin.modules.mnt.service.dto.DatabaseQueryCriteria;
import com.admin.modules.mnt.util.SqlUtils;
import com.admin.system.mnt.mapper.IDatabaseMapper;
import com.admin.system.mnt.model.DatabaseModel;
import com.admin.system.mnt.service.IDatabaseService;
import com.admin.utils.DozerUtils;
import com.admin.utils.FileUtil;
import com.admin.utils.PageUtil;
import com.admin.utils.ValidationUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AdminDatabaseServiceImpl extends ServiceImpl<IDatabaseMapper, DatabaseModel> implements IDatabaseService {
    private final Mapper mapper;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(DatabaseQueryCriteria criteria, IPage pageable) {
        IPage<DatabaseModel> page = this.page(pageable, this.buildWrapper(criteria));
        List<DatabaseDto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), DatabaseDto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    @Override
    public List<DatabaseDto> queryAll(DatabaseQueryCriteria criteria) {
        List<DatabaseModel> users = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, users, DatabaseDto.class);
    }

    private QueryWrapper<DatabaseModel> buildWrapper(DatabaseQueryCriteria criteria) {
        QueryWrapper<DatabaseModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<DatabaseModel>();
            query.lambda().like(StringUtils.isNotEmpty(criteria.getName()), DatabaseModel::getName, criteria.getName())
                    .like(StringUtils.isNotEmpty(criteria.getJdbcUrl()), DatabaseModel::getJdbcUrl,
                            criteria.getJdbcUrl())
                    .between(haveTime, DatabaseModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    public DatabaseDto findById(String id) {
        DatabaseModel database = Optional.ofNullable(this.getById(id)).orElseGet(DatabaseModel::new);
        ValidationUtil.isNull(database.getId(), "Database", "id", id);
        return mapper.map(database, DatabaseDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DatabaseModel resources) {
        resources.setId(IdUtil.simpleUUID());
        this.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DatabaseModel resources) {
        DatabaseModel database = Optional.ofNullable(this.getById(resources.getId())).orElseGet(DatabaseModel::new);
        ValidationUtil.isNull(database.getId(), "Database", "id", resources.getId());
        database.copy(resources);
        this.saveOrUpdate(database);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        this.removeByIds(ids);
    }

    @Override
    public boolean testConnection(DatabaseModel resources) {
        try {
            return SqlUtils.testConnection(resources.getJdbcUrl(), resources.getUserName(), resources.getPwd());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void download(List<DatabaseDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DatabaseDto databaseDto : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("数据库名称", databaseDto.getName());
            map.put("数据库连接地址", databaseDto.getJdbcUrl());
            map.put("用户名", databaseDto.getUserName());
            map.put("创建日期", databaseDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
