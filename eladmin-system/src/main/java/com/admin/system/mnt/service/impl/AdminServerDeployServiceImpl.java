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

import com.admin.modules.mnt.service.dto.ServerDeployDto;
import com.admin.modules.mnt.service.dto.ServerDeployQueryCriteria;
import com.admin.modules.mnt.util.ExecuteShellUtil;
import com.admin.system.mnt.mapper.IServerDeployMapper;
import com.admin.system.mnt.model.ServerDeployModel;
import com.admin.system.mnt.service.IServerDeployService;
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
public class AdminServerDeployServiceImpl extends ServiceImpl<IServerDeployMapper, ServerDeployModel>
        implements IServerDeployService {
    private final Mapper mapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object queryAll(ServerDeployQueryCriteria criteria, IPage pageable) {
        IPage<ServerDeployModel> page = this.page(pageable, this.buildWrapper(criteria));
        List<ServerDeployDto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), ServerDeployDto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    @Override
    public List<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria) {
        List<ServerDeployModel> users = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, users, ServerDeployDto.class);
    }

    private QueryWrapper<ServerDeployModel> buildWrapper(ServerDeployQueryCriteria criteria) {
        QueryWrapper<ServerDeployModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<ServerDeployModel>();
            query.lambda()
                    .nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                            i -> i.like(ServerDeployModel::getName, criteria.getBlurry()).or()
                                    .like(ServerDeployModel::getIp, criteria.getBlurry()).or()
                                    .like(ServerDeployModel::getAccount, criteria.getBlurry()))
                    .between(haveTime, ServerDeployModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    public ServerDeployDto findById(Long id) {
        ServerDeployModel server = Optional.ofNullable(this.getById(id)).orElseGet(ServerDeployModel::new);
        ValidationUtil.isNull(server.getId(), "ServerDeploy", "id", id);
        return mapper.map(server, ServerDeployDto.class);
    }

    @Override
    public ServerDeployDto findByIp(String ip) {
        QueryWrapper<ServerDeployModel> query = new QueryWrapper<ServerDeployModel>();
        query.lambda().eq(ServerDeployModel::getIp, ip);
        ServerDeployModel deploy = this.getOne(query);
        return mapper.map(deploy, ServerDeployDto.class);
    }

    @Override
    public Boolean testConnect(ServerDeployModel resources) {
        ExecuteShellUtil executeShellUtil = null;
        try {
            executeShellUtil = new ExecuteShellUtil(resources.getIp(), resources.getAccount(), resources.getPassword(),
                    resources.getPort());
            return executeShellUtil.execute("ls") == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (executeShellUtil != null) {
                executeShellUtil.close();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ServerDeployModel resources) {
        this.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ServerDeployModel resources) {
        ServerDeployModel serverDeploy = Optional.ofNullable(this.getById(resources.getId())).orElseGet(ServerDeployModel::new);
        ValidationUtil.isNull(serverDeploy.getId(), "ServerDeploy", "id", resources.getId());
        serverDeploy.copy(resources);
        this.saveOrUpdate(serverDeploy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void download(List<ServerDeployDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ServerDeployDto deployDto : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("服务器名称", deployDto.getName());
            map.put("服务器IP", deployDto.getIp());
            map.put("端口", deployDto.getPort());
            map.put("账号", deployDto.getAccount());
            map.put("创建日期", deployDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
