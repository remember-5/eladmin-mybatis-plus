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
import java.sql.Timestamp;

/**
 * <p>
 * Sm.Ms图床
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_PICTURE)
@ApiModel(value = "Picture对象", description = "Sm.Ms图床")
public class PictureModel extends Model<PictureModel> implements Serializable {
    private static final long serialVersionUID = -3456905088513476291L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.PICTURE_PICTURE_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用于删除的URL")
    @TableField(value = AdminFields.PICTURE_DELETE_URL)
    private String deleteUrl;

    @ApiModelProperty(value = "文件名")
    private String filename;

    @ApiModelProperty(value = "图片高")
    private String height;

    @ApiModelProperty(value = "图片大小")
    private String size;

    @ApiModelProperty(value = "图片url")
    private String url;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "图片宽")
    private String width;

    @ApiModelProperty(value = "文件的MD5值")
    private String md5Code;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = AdminFields.CREATE_TIME, fill = FieldFill.INSERT)
    private Timestamp createTime;
}
