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
package com.admin.service;

import com.admin.model.ColumnInfoModel;
import com.admin.model.GenConfigModel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Administrator
 * @since 2020-05-30
 */
public interface IGeneratorService extends IService<ColumnInfoModel> {

    /**
     * @return
     */
    Object getTables();

    /**
     * @param name
     * @param startEnd
     * @return
     */
    Object getTables(String name, int[] startEnd);

    /**
     * @param tableName
     * @return
     */
    List<ColumnInfoModel> getColumns(String tableName);

    /**
     * @param tableName
     * @return
     */
    List<ColumnInfoModel> query(String tableName);

    /**
     * @param columnInfos
     * @param columnInfoList
     */
    void sync(List<ColumnInfoModel> columnInfos, List<ColumnInfoModel> columnInfoList);

    /**
     * @param columnInfos
     */
    void save(List<ColumnInfoModel> columnInfos);

    /**
     * @param genConfig
     * @param columns
     */
    void generator(GenConfigModel genConfig, List<ColumnInfoModel> columns);

    /**
     * @param genConfig
     * @param columns
     * @return
     */
    ResponseEntity<Object> preview(GenConfigModel genConfig, List<ColumnInfoModel> columns);

    /**
     * @param genConfig
     * @param columns
     * @param request
     * @param response
     */
    void download(GenConfigModel genConfig, List<ColumnInfoModel> columns, HttpServletRequest request,
                  HttpServletResponse response);

}
