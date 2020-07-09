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

import com.admin.model.GenConfigModel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author adyfang
 * @since 2020-05-30
 */
public interface IGenConfigService extends IService<GenConfigModel> {

    /**
     * @param tableName
     * @return
     */
    GenConfigModel find(String tableName);

    /**
     * @param tableName
     * @param genConfig
     * @return
     */
    GenConfigModel update(String tableName, GenConfigModel genConfig);

}
