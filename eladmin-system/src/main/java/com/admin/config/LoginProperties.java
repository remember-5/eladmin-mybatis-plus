/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version loginCode.length.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-loginCode.length.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.admin.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置文件读取
 *
 * @author liaojinlong
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "login", ignoreUnknownFields = false)
public class LoginProperties {

    /**
     * 账号单用户 登录
     */
    private boolean singleLogin;

    /**
     * 用户登录信息缓存
     */
    private boolean cacheEnable;

    /**
     * 验证码配置
     */
    private LoginCode loginCode;


}
