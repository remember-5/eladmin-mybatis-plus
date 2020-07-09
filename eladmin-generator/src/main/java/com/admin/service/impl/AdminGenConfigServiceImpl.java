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
package com.admin.service.impl;

import com.admin.mapper.IGenConfigMapper;
import com.admin.model.GenConfigModel;
import com.admin.service.IGenConfigService;
import com.admin.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author Administrator
 * @since 2020-05-30
 */
@Service
@RequiredArgsConstructor
public class AdminGenConfigServiceImpl extends ServiceImpl<IGenConfigMapper, GenConfigModel>
        implements IGenConfigService {

    @Override
    public GenConfigModel find(String tableName) {
        QueryWrapper<GenConfigModel> query = new QueryWrapper<GenConfigModel>();
        query.lambda().eq(GenConfigModel::getTableName, tableName).orderByAsc(GenConfigModel::getId);
        GenConfigModel genConfig = this.baseMapper.selectOne(query);
        if (genConfig == null) {
            return new GenConfigModel(tableName);
        }
        return genConfig;
    }

    @Override
    public GenConfigModel update(String tableName, GenConfigModel genConfig) {
        // 如果 api 路径为空，则自动生成路径
        if (StringUtils.isBlank(genConfig.getApiPath())) {
            String separator = File.separator;
            String[] paths;
            String symbol = "\\";
            if (symbol.equals(separator)) {
                paths = genConfig.getPath().split("\\\\");
            } else {
                paths = genConfig.getPath().split(File.separator);
            }
            StringBuilder api = new StringBuilder();
            for (String path : paths) {
                api.append(path);
                api.append(separator);
                if ("src".equals(path)) {
                    api.append("api");
                    break;
                }
            }
            genConfig.setApiPath(api.toString());
        }
        this.saveOrUpdate(genConfig);
        return genConfig;
    }
}
