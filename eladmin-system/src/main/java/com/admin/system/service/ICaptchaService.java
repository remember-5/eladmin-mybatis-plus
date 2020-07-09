package com.admin.system.service;

import com.admin.config.LoginCode;
import com.wf.captcha.base.Captcha;

/**
 * @author wangjiahao
 * @date 2020/6/28
 */
public interface ICaptchaService {

    /**
     * /
     *
     * @return /
     */
    Captcha getCaptcha();

    /**
     * /
     *
     * @param loginCode /
     * @return /
     */
    Captcha switchCaptcha(LoginCode loginCode);

}
