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
package com.admin.model;

import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(AdminFields.TABLE_CODE_GEN_CONFIG)
public class GenConfigModel extends Model<GenConfigModel> implements Serializable {
    private static final long serialVersionUID = 4970400018974735201L;

    public GenConfigModel(String tableName) {
        this.tableName = tableName;
    }

    @ApiModelProperty(hidden = true)
    @EqualsAndHashCode.Include
    @TableId(value = AdminFields.GEN_CONFIG_CONFIG_ID, type = IdType.AUTO)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "表名")
    @TableField(value = AdminFields.GEN_CONFIG_TABLE_NAME)
    private String tableName;

    @ApiModelProperty(value = "接口名称")
    @TableField(value = AdminFields.GEN_CONFIG_API_ALIAS)
    private String apiAlias;

    @NotBlank
    @ApiModelProperty(value = "包路径")
    private String pack;

    @NotBlank
    @ApiModelProperty(value = "模块名")
    @TableField(value = AdminFields.GEN_CONFIG_MODULE_NAME)
    private String moduleName;

    @NotBlank
    @ApiModelProperty(value = "前端文件路径")
    private String path;

    @ApiModelProperty(value = "前端文件路径")
    @TableField(value = AdminFields.GEN_CONFIG_API_PATH)
    private String apiPath;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "表前缀")
    private String prefix;

    @ApiModelProperty(value = "是否覆盖")
    private Boolean cover = false;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
