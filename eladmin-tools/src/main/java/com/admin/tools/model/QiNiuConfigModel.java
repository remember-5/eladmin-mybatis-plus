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
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 七牛云配置
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_QINIU_CONFIG)
@ApiModel(value = "QiniuConfig对象", description = "七牛云配置")
public class QiNiuConfigModel extends Model<QiNiuConfigModel> implements Serializable {
    private static final long serialVersionUID = 5964806466191471941L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.QINIU_CONFIG_CONFIG_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "accessKey")
    @NotBlank
    @TableField(value = AdminFields.QINIU_CONFIG_ACCESS_KEY)
    private String accessKey;

    @ApiModelProperty(value = "存储空间名称作为唯一的 Bucket 识别符")
    @NotBlank
    private String bucket;

    @ApiModelProperty(value = "外链域名，可自定义，需在七牛云绑定")
    @NotBlank
    private String host;

    @ApiModelProperty(value = "secretKey")
    @NotBlank
    @TableField(value = AdminFields.QINIU_CONFIG_SECRET_KEY)
    private String secretKey;

    @ApiModelProperty(value = "空间类型：公开/私有")
    private String type = "公开";

    /**
     * Zone表示与机房的对应关系 华东 Zone.zone0() 华北 Zone.zone1() 华南 Zone.zone2() 北美
     * Zone.zoneNa0() 东南亚 Zone.zoneAs0()
     */
    @ApiModelProperty(value = "Zone表示与机房的对应关系")
    @NotBlank
    private String zone;

}
