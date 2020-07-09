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

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.admin.dto.PictureQueryCriteria;
import com.admin.exception.BadRequestException;
import com.admin.tools.mapper.IPictureMapper;
import com.admin.tools.model.PictureModel;
import com.admin.tools.service.IPictureService;
import com.admin.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
@Service(value = "pictureService")
@CacheConfig(cacheNames = "picture")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminPictureServiceImpl extends ServiceImpl<IPictureMapper, PictureModel> implements IPictureService {

    @Value("${smms.token}")
    private String token;

    private static final String SUCCESS = "success";

    private static final String CODE = "code";

    private static final String MSG = "message";

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(PictureQueryCriteria criteria, IPage pageable) {
        IPage page = this.page(pageable, this.buildWrapper(criteria));
        return PageUtil.toPage(page.getRecords(), page.getTotal());
    }

    @Override
    public List<PictureModel> queryAll(PictureQueryCriteria criteria) {
        return this.list(this.buildWrapper(criteria));
    }

    private QueryWrapper<PictureModel> buildWrapper(PictureQueryCriteria criteria) {
        QueryWrapper<PictureModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<PictureModel>();
            query.lambda()
                    .like(StringUtils.isNotEmpty(criteria.getFilename()), PictureModel::getFilename,
                            criteria.getFilename())
                    .like(StringUtils.isNotEmpty(criteria.getUsername()), PictureModel::getUsername,
                            criteria.getUsername())
                    .between(haveTime, PictureModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public PictureModel upload(MultipartFile multipartFile, String username) {
        File file = FileUtil.toFile(multipartFile);
        // 验证是否重复上传
        QueryWrapper<PictureModel> query = new QueryWrapper<PictureModel>();
        query.lambda().eq(PictureModel::getMd5Code, FileUtil.getMd5(file));
        PictureModel picture = this.getOne(query);
        if (picture != null) {
            return picture;
        }
        HashMap<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("smfile", file);
        // 上传文件
        String result = HttpRequest.post(ElAdminConstant.Url.SM_MS_URL + "/v2/upload").header("Authorization", token)
                .form(paramMap).timeout(20000).execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if (!jsonObject.get(CODE).toString().equals(SUCCESS)) {
            throw new BadRequestException(TranslatorUtil.translate(jsonObject.get(MSG).toString()));
        }
        picture = JSON.parseObject(jsonObject.get("data").toString(), PictureModel.class);
        picture.setSize(FileUtil.getSize(Integer.parseInt(picture.getSize())));
        picture.setUsername(username);
        picture.setMd5Code(FileUtil.getMd5(file));
        picture.setFilename(FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()) + "."
                + FileUtil.getExtensionName(multipartFile.getOriginalFilename()));
        this.saveOrUpdate(picture);
        // 删除临时文件
        FileUtil.del(file);
        return picture;

    }

    @Override
    public PictureModel findById(Long id) {
        PictureModel picture = Optional.ofNullable(this.getById(id)).orElseGet(PictureModel::new);
        ValidationUtil.isNull(picture.getId(), "Picture", "id", id);
        return picture;
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            PictureModel picture = this.getById(id);
            try {
                HttpUtil.get(picture.getDeleteUrl());
                this.removeById(id);
            } catch (Exception e) {
                this.removeById(id);
            }
        }
    }

    @Override
    public void synchronize() {
        // 链式构建请求
        String result = HttpRequest.get(ElAdminConstant.Url.SM_MS_URL + "/v2/upload_history")
                // 头信息，多个头信息多次调用此方法即可
                .header("Authorization", token).timeout(20000).execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        List<PictureModel> pictures = JSON.parseArray(jsonObject.get("data").toString(), PictureModel.class);
        for (PictureModel picture : pictures) {
            QueryWrapper<PictureModel> query = new QueryWrapper<PictureModel>();
            query.lambda().eq(PictureModel::getUrl, picture.getUrl());
            if (CollectionUtils.isEmpty(this.list(query))) {
                picture.setSize(FileUtil.getSize(Integer.parseInt(picture.getSize())));
                picture.setUsername("System Sync");
                picture.setMd5Code(null);
                this.save(picture);
            }
        }
    }

    @Override
    public void download(List<PictureModel> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PictureModel picture : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", picture.getFilename());
            map.put("图片地址", picture.getUrl());
            map.put("文件大小", picture.getSize());
            map.put("操作人", picture.getUsername());
            map.put("高度", picture.getHeight());
            map.put("宽度", picture.getWidth());
            map.put("删除地址", picture.getDeleteUrl());
            map.put("创建日期", picture.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
