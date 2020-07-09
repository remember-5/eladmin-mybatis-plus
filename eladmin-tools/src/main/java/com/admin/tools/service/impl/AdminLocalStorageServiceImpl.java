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
package com.admin.tools.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.admin.config.FileProperties;
import com.admin.dto.LocalStorageDto;
import com.admin.dto.LocalStorageQueryCriteria;
import com.admin.exception.BadRequestException;
import com.admin.tools.mapper.ILocalStorageMapper;
import com.admin.tools.model.LocalStorageModel;
import com.admin.tools.service.ILocalStorageService;
import com.admin.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
@Service
@RequiredArgsConstructor
public class AdminLocalStorageServiceImpl extends ServiceImpl<ILocalStorageMapper, LocalStorageModel>
        implements ILocalStorageService {

    private final FileProperties properties;

    private final Mapper mapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object queryAll(LocalStorageQueryCriteria criteria, IPage pageable) {
        IPage page = this.page(pageable, buildWrapper(criteria));
        List<LocalStorageDto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), LocalStorageDto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    @Override
    public List<LocalStorageDto> queryAll(LocalStorageQueryCriteria criteria) {
        List<LocalStorageModel> list = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, list, LocalStorageDto.class);
    }

    private QueryWrapper<LocalStorageModel> buildWrapper(LocalStorageQueryCriteria criteria) {
        QueryWrapper<LocalStorageModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<LocalStorageModel>();
            query.lambda()
                    .nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                            i -> i.like(LocalStorageModel::getName, criteria.getBlurry()).or()
                                    .like(LocalStorageModel::getSuffix, criteria.getBlurry()).or()
                                    .like(LocalStorageModel::getType, criteria.getBlurry()).or()
                                    .like(LocalStorageModel::getCreateBy, criteria.getBlurry()).or()
                                    .like(LocalStorageModel::getSize, criteria.getBlurry()))
                    .between(haveTime, LocalStorageModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    public LocalStorageDto findById(Long id) {
        LocalStorageModel localStorage = Optional.ofNullable(this.getById(id)).orElseGet(LocalStorageModel::new);
        ValidationUtil.isNull(localStorage.getId(), "LocalStorage", "id", id);
        return mapper.map(localStorage, LocalStorageDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(String name, MultipartFile multipartFile) {
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, properties.getPath().getPath() + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("上传失败");
        }
        try {
            name = StringUtils.isBlank(name) ? FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) : name;
            LocalStorageModel localStorage = new LocalStorageModel(file.getName(), name, suffix, file.getPath(), type,
                    FileUtil.getSize(multipartFile.getSize()));
            this.save(localStorage);
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LocalStorageModel resources) {
        LocalStorageModel localStorage = Optional.ofNullable(this.getById(resources.getId()))
                .orElseGet(LocalStorageModel::new);
        ValidationUtil.isNull(localStorage.getId(), "LocalStorage", "id", resources.getId());
        localStorage.copy(resources);
        this.saveOrUpdate(localStorage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            LocalStorageModel storage = Optional.ofNullable(this.getById(id)).orElseGet(LocalStorageModel::new);
            FileUtil.del(storage.getPath());
        }
        this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public void download(List<LocalStorageDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LocalStorageDto localStorageDTO : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", localStorageDTO.getRealName());
            map.put("备注名", localStorageDTO.getName());
            map.put("文件类型", localStorageDTO.getType());
            map.put("文件大小", localStorageDTO.getSize());
            map.put("创建者", localStorageDTO.getCreateBy());
            map.put("创建日期", localStorageDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
