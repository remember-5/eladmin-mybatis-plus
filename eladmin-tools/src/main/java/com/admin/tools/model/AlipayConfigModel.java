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
package com.admin.tools.model;

import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 支付宝配置类
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_ALIPAY_CONFIG)
@ApiModel(value = "AlipayConfig对象", description = "支付宝配置类")
public class AlipayConfigModel extends Model<AlipayConfigModel> implements Serializable {
    private static final long serialVersionUID = -1764655016031829031L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.ALIPAY_CONFIG_CONFIG_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "应用ID")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_APP_ID)
    private String appId;

    @ApiModelProperty(value = "编码")
    private String charset = "utf-8";

    @ApiModelProperty(value = "类型")
    private String format = "JSON";

    @ApiModelProperty(value = "支付宝开放安全地址", hidden = true)
    @TableField(value = AdminFields.ALIPAY_CONFIG_GATEWAY_URL)
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    @ApiModelProperty(value = "异步通知地址")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_NOTIFY_URL)
    private String notifyUrl;

    @ApiModelProperty(value = "商户私钥")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_PRIVATE_KEY)
    private String privateKey;

    @ApiModelProperty(value = "支付宝公钥")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_PUBLIC_KEY)
    private String publicKey;

    @ApiModelProperty(value = "订单完成后返回的页面")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_RETURN_URL)
    private String returnUrl;

    @ApiModelProperty(value = "签名方式")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_SIGN_TYPE)
    private String signType = "RSA2";

    @ApiModelProperty(value = "商户号")
    @NotBlank
    @TableField(value = AdminFields.ALIPAY_CONFIG_SYS_SERVICE_PROVIDER_ID)
    private String sysServiceProviderId;

}
