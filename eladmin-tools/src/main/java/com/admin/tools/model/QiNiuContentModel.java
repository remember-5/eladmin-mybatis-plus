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
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 七牛云文件存储
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_QINIU_CONTENT)
@ApiModel(value = "QiniuContent对象", description = "七牛云文件存储")
public class QiNiuContentModel extends Model<QiNiuContentModel> implements Serializable {
    private static final long serialVersionUID = -4267104287860943547L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.QINIU_CONTENT_CONTENT_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "空间名")
    private String bucket;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "大小")
    private String size;

    @ApiModelProperty(value = "空间类型：公开/私有")
    private String type = "公开";

    @ApiModelProperty(value = "创建或更新时间")
    @TableField(value = AdminFields.UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "文件地址")
    private String url;

    @ApiModelProperty(value = "文件类型")
    private String suffix;

}
