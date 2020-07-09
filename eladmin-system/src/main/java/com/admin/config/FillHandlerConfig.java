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
package com.admin.config;

import com.admin.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
@Component
public class FillHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        setFieldValByName("createTime", currentTime, metaObject);
        setFieldValByName("createBy", getUsername(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String userName = SecurityUtils.getCurrentUsername();
        this.setFieldValByName("updateTime", currentTime, metaObject);
        setFieldValByName("updatedBy", userName, metaObject);
    }

    private String getUsername() {
        try {
            return SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            return "";
        }
    }
}