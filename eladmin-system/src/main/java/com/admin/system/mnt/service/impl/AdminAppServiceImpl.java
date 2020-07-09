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

import com.admin.exception.BadRequestException;
import com.admin.modules.mnt.service.dto.AppDto;
import com.admin.modules.mnt.service.dto.AppQueryCriteria;
import com.admin.system.mnt.mapper.IAppMapper;
import com.admin.system.mnt.model.AppModel;
import com.admin.system.mnt.service.IAppService;
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
public class AdminAppServiceImpl extends ServiceImpl<IAppMapper, AppModel> implements IAppService {
    private final Mapper mapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object queryAll(AppQueryCriteria criteria, IPage pageable) {
        IPage<AppModel> page = this.page(pageable, this.buildWrapper(criteria));
        List<AppDto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), AppDto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    @Override
    public List<AppDto> queryAll(AppQueryCriteria criteria) {
        List<AppModel> users = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, users, AppDto.class);
    }

    private QueryWrapper<AppModel> buildWrapper(AppQueryCriteria criteria) {
        QueryWrapper<AppModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<AppModel>();
            query.lambda().like(StringUtils.isNotEmpty(criteria.getName()), AppModel::getName, criteria.getName())
                    .between(haveTime, AppModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    public AppDto findById(Long id) {
        AppModel app = Optional.ofNullable(this.getById(id)).orElseGet(AppModel::new);
        ValidationUtil.isNull(app.getId(), "App", "id", id);
        return mapper.map(app, AppDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AppModel resources) {
        verification(resources);
        this.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AppModel resources) {
        verification(resources);
        AppModel app = Optional.ofNullable(this.getById(resources.getId())).orElseGet(AppModel::new);
        ValidationUtil.isNull(app.getId(), "App", "id", resources.getId());
        app.copy(resources);
        this.saveOrUpdate(app);
    }

    private void verification(AppModel resources) {
        String opt = "/opt";
        String home = "/home";
        if (!(resources.getUploadPath().startsWith(opt) || resources.getUploadPath().startsWith(home))) {
            throw new BadRequestException("文件只能上传在opt目录或者home目录 ");
        }
        if (!(resources.getDeployPath().startsWith(opt) || resources.getDeployPath().startsWith(home))) {
            throw new BadRequestException("文件只能部署在opt目录或者home目录 ");
        }
        if (!(resources.getBackupPath().startsWith(opt) || resources.getBackupPath().startsWith(home))) {
            throw new BadRequestException("文件只能备份在opt目录或者home目录 ");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public void download(List<AppDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AppDto appDto : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("应用名称", appDto.getName());
            map.put("端口", appDto.getPort());
            map.put("上传目录", appDto.getUploadPath());
            map.put("部署目录", appDto.getDeployPath());
            map.put("备份目录", appDto.getBackupPath());
            map.put("启动脚本", appDto.getStartScript());
            map.put("部署脚本", appDto.getDeployScript());
            map.put("创建日期", appDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
