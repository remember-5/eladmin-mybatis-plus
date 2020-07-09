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

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.admin.exception.BadRequestException;
import com.admin.tools.mapper.IEmailConfigMapper;
import com.admin.tools.model.EmailConfigModel;
import com.admin.tools.service.IEmailService;
import com.admin.utils.EncryptUtils;
import com.admin.vo.EmailVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
@Service
@CacheConfig(cacheNames = "email")
public class AdminEmailServiceImpl extends ServiceImpl<IEmailConfigMapper, EmailConfigModel> implements IEmailService {

    @Override
    @CachePut(key = "'config'")
    @Transactional(rollbackFor = Exception.class)
    public EmailConfigModel config(EmailConfigModel emailConfig, EmailConfigModel old) throws Exception {
        emailConfig.setId(1L);
        if (!emailConfig.getPass().equals(old.getPass())) {
            // 对称加密
            emailConfig.setPass(EncryptUtils.desEncrypt(emailConfig.getPass()));
        }
        this.saveOrUpdate(emailConfig);
        return emailConfig;
    }

    @Override
    @Cacheable(key = "'config'")
    public EmailConfigModel find() {
        return Optional.ofNullable(this.getById(1L)).orElseGet(EmailConfigModel::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo, EmailConfigModel emailConfig) {
        if (emailConfig == null) {
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        account.setUser(emailConfig.getUser());
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser() + "<" + emailConfig.getFromUser() + ">");
        // ssl方式发送
        account.setSslEnable(true);
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        String content = emailVo.getContent();
        // 发送
        try {
            int size = emailVo.getTos().size();
            Mail.create(account).setTos(emailVo.getTos().toArray(new String[size])).setTitle(emailVo.getSubject())
                    .setContent(content).setHtml(true)
                    // 关闭session
                    .setUseGlobalSession(false).send();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
