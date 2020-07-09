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

import com.admin.dto.QiniuQueryCriteria;
import com.admin.exception.BadRequestException;
import com.admin.tools.mapper.IQiNiuContentMapper;
import com.admin.tools.model.QiNiuConfigModel;
import com.admin.tools.model.QiNiuContentModel;
import com.admin.tools.service.IQiNiuConfigService;
import com.admin.tools.service.IQiNiuService;
import com.admin.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
@Service
@CacheConfig(cacheNames = "qiNiu")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminQiNiuServiceImpl extends ServiceImpl<IQiNiuContentMapper, QiNiuContentModel>
        implements IQiNiuService {

    private IQiNiuConfigService qiNiuConfigService;

    @Value("${qiniu.max-size}")
    private Long maxSize;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(QiniuQueryCriteria criteria, IPage pageable) {
        IPage page = this.page(pageable, this.buildWrapper(criteria));
        return PageUtil.toPage(page.getRecords(), page.getTotal());
    }

    @Override
    public List<QiNiuContentModel> queryAll(QiniuQueryCriteria criteria) {
        return this.list(buildWrapper(criteria));
    }

    private QueryWrapper<QiNiuContentModel> buildWrapper(QiniuQueryCriteria criteria) {
        QueryWrapper<QiNiuContentModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<QiNiuContentModel>();
            query.lambda()
                    .like(StringUtils.isNotEmpty(criteria.getKey()), QiNiuContentModel::getName, criteria.getKey())
                    .between(haveTime, QiNiuContentModel::getUpdateTime, start, end);
        }
        return query;
    }

    @Override
    @Cacheable(key = "'config'")
    public QiNiuConfigModel find() {
        return Optional.ofNullable(qiNiuConfigService.getById(1L)).orElseGet(QiNiuConfigModel::new);
    }

    @Override
    @CachePut(key = "'config'")
    @Transactional(rollbackFor = Exception.class)
    public QiNiuConfigModel config(QiNiuConfigModel qiniuConfig) {
        qiniuConfig.setId(1L);
        String http = "http://", https = "https://";
        if (!(qiniuConfig.getHost().toLowerCase().startsWith(http)
                || qiniuConfig.getHost().toLowerCase().startsWith(https))) {
            throw new BadRequestException("外链域名必须以http://或者https://开头");
        }
        qiniuConfig.setId(1L);
        qiNiuConfigService.saveOrUpdate(qiniuConfig);
        return qiniuConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QiNiuContentModel upload(MultipartFile file, QiNiuConfigModel qiniuConfig) {
        FileUtil.checkSize(maxSize, file.getSize());
        if (qiniuConfig.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(qiniuConfig.getZone()));
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        String upToken = auth.uploadToken(qiniuConfig.getBucket());
        try {
            String key = file.getOriginalFilename();
            QueryWrapper<QiNiuContentModel> query = new QueryWrapper<QiNiuContentModel>();
            query.lambda().eq(QiNiuContentModel::getName, key);
            if (this.getOne(query) != null) {
                key = QiNiuUtil.getKey(key);
            }
            Response response = uploadManager.put(file.getBytes(), key, upToken);
            // 解析上传成功的结果

            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            QueryWrapper<QiNiuContentModel> query2 = new QueryWrapper<QiNiuContentModel>();
            query2.lambda().eq(QiNiuContentModel::getName, FileUtil.getFileNameNoEx(putRet.key));
            QiNiuContentModel content = this.getOne(query);
            if (content == null) {
                // 存入数据库
                QiNiuContentModel qiniuContent = new QiNiuContentModel();
                qiniuContent.setSuffix(FileUtil.getExtensionName(putRet.key));
                qiniuContent.setBucket(qiniuConfig.getBucket());
                qiniuContent.setType(qiniuConfig.getType());
                qiniuContent.setName(FileUtil.getFileNameNoEx(putRet.key));
                qiniuContent.setUrl(qiniuConfig.getHost() + "/" + putRet.key);
                qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(file.getSize() + "")));
                this.saveOrUpdate(qiniuContent);
                return qiniuContent;
            }
            return content;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public QiNiuContentModel findByContentId(Long id) {
        QiNiuContentModel qiniuContent = Optional.ofNullable(this.getById(id)).orElseGet(QiNiuContentModel::new);
        ValidationUtil.isNull(qiniuContent.getId(), "QiniuContent", "id", id);
        return qiniuContent;
    }

    @Override
    public String download(QiNiuContentModel content, QiNiuConfigModel config) {
        String finalUrl;
        String type = "公开";
        if (type.equals(content.getType())) {
            finalUrl = content.getUrl();
        } else {
            Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
            // 1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            finalUrl = auth.privateDownloadUrl(content.getUrl(), expireInSeconds);
        }
        return finalUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(QiNiuContentModel content, QiNiuConfigModel config) {
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(content.getBucket(), content.getName() + "." + content.getSuffix());
            this.removeById(content.getId());
        } catch (QiniuException ex) {
            this.removeById(content.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronize(QiNiuConfigModel config) {
        if (config.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        // 文件名前缀
        String prefix = "";
        // 每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        // 指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        // 列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(config.getBucket(),
                prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            // 处理获取的file list结果
            QiNiuContentModel qiniuContent = null;
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                QueryWrapper<QiNiuContentModel> query = new QueryWrapper<QiNiuContentModel>();
                query.lambda().eq(QiNiuContentModel::getName, FileUtil.getFileNameNoEx(item.key));
                if (this.getOne(query) == null) {
                    qiniuContent = new QiNiuContentModel();
                    qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(item.fsize + "")));
                    qiniuContent.setSuffix(FileUtil.getExtensionName(item.key));
                    qiniuContent.setName(FileUtil.getFileNameNoEx(item.key));
                    qiniuContent.setType(config.getType());
                    qiniuContent.setBucket(config.getBucket());
                    qiniuContent.setUrl(config.getHost() + "/" + item.key);
                    this.save(qiniuContent);
                }
            }
        }
    }

    @Override
    public void deleteAll(Long[] ids, QiNiuConfigModel config) {
        for (Long id : ids) {
            this.delete(findByContentId(id), config);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String type) {
        UpdateWrapper<QiNiuConfigModel> query = new UpdateWrapper<QiNiuConfigModel>();
        query.lambda().eq(QiNiuConfigModel::getType, type);
        qiNiuConfigService.update(query);
    }

    @Override
    public void downloadList(List<QiNiuContentModel> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QiNiuContentModel content : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", content.getName());
            map.put("文件类型", content.getSuffix());
            map.put("空间名称", content.getBucket());
            map.put("文件大小", content.getSize());
            map.put("空间类型", content.getType());
            map.put("创建日期", content.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
