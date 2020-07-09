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
package com.admin.tools.service;

import com.admin.dto.QiniuQueryCriteria;
import com.admin.tools.model.QiNiuConfigModel;
import com.admin.tools.model.QiNiuContentModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
public interface IQiNiuService {
    /**
     * 查配置
     *
     * @return
     */
    QiNiuConfigModel find();

    /**
     * 修改配置
     *
     * @param qiniuConfig 配置
     * @return QiniuConfig
     */
    QiNiuConfigModel config(QiNiuConfigModel qiniuConfig);

    /**
     * 上传文件
     *
     * @param file        文件
     * @param qiniuConfig 配置
     * @return QiniuContent
     */
    QiNiuContentModel upload(MultipartFile file, QiNiuConfigModel qiniuConfig);

    /**
     * 导出数据
     *
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void downloadList(List<QiNiuContentModel> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 查询文件
     *
     * @param id 文件ID
     * @return QiniuContent
     */
    QiNiuContentModel findByContentId(Long id);

    /**
     * 下载文件
     *
     * @param content 文件信息
     * @param config  配置
     * @return String
     */
    String download(QiNiuContentModel content, QiNiuConfigModel config);

    /**
     * 删除文件
     *
     * @param content 文件
     * @param config  配置
     */
    void delete(QiNiuContentModel content, QiNiuConfigModel config);

    /**
     * 同步数据
     *
     * @param config 配置
     */
    void synchronize(QiNiuConfigModel config);

    /**
     * 更新数据
     *
     * @param type 类型
     */
    void update(String type);

    /**
     * 删除文件
     *
     * @param ids    文件ID数组
     * @param config 配置
     */
    void deleteAll(Long[] ids, QiNiuConfigModel config);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<QiNiuContentModel> queryAll(QiniuQueryCriteria criteria);

    /**
     * 分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    @SuppressWarnings("rawtypes")
    Object queryAll(QiniuQueryCriteria criteria, IPage pageable);

}
