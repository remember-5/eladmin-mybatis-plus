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

import com.admin.tools.model.EmailConfigModel;
import com.admin.vo.EmailVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
public interface IEmailService extends IService<EmailConfigModel> {

    /**
     * 更新邮件配置
     *
     * @param emailConfig 邮件配置
     * @param old         旧的配置
     * @return EmailConfig
     * @throws Exception /
     */
    EmailConfigModel config(EmailConfigModel emailConfig, EmailConfigModel old) throws Exception;

    /**
     * 查询配置
     *
     * @return EmailConfig 邮件配置
     */
    EmailConfigModel find();

    /**
     * 发送邮件
     *
     * @param emailVo     邮件发送的内容
     * @param emailConfig 邮件配置
     * @throws Exception /
     */
    void send(EmailVo emailVo, EmailConfigModel emailConfig);
}
