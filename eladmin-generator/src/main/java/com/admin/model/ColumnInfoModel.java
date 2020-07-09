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
import com.admin.utils.GenUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_CODE_COLUMN_CONFIG)
public class ColumnInfoModel extends Model<ColumnInfoModel> implements Serializable {
    private static final long serialVersionUID = -8012818091184123121L;

    @ApiModelProperty(hidden = true)
    @EqualsAndHashCode.Include
    @TableId(value = AdminFields.CODE_COLUMN_COLUMN_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "表名")
    @TableField(value = AdminFields.CODE_COLUMN_TABLE_NAME)
    private String tableName;

    @ApiModelProperty(value = "数据库字段名称")
    @TableField(value = AdminFields.CODE_COLUMN_COLUMN_NAME)
    private String columnName;

    @ApiModelProperty(value = "数据库字段类型")
    @TableField(value = AdminFields.CODE_COLUMN_COLUMN_TYPE)
    private String columnType;

    @ApiModelProperty(value = "数据库字段键类型")
    @TableField(value = AdminFields.CODE_COLUMN_KEY_TYPE)
    private String keyType;

    @ApiModelProperty(value = "字段额外的参数")
    private String extra;

    @ApiModelProperty(value = "数据库字段描述")
    private String remark;

    @ApiModelProperty(value = "是否必填")
    @TableField(value = AdminFields.CODE_COLUMN_NOT_NULL)
    private Boolean notNull;

    @ApiModelProperty(value = "是否在列表显示")
    @TableField(value = AdminFields.CODE_COLUMN_LIST_SHOW)
    private Boolean listShow;

    @ApiModelProperty(value = "是否表单显示")
    @TableField(value = AdminFields.CODE_COLUMN_FORM_SHOW)
    private Boolean formShow;

    @ApiModelProperty(value = "表单类型")
    @TableField(value = AdminFields.CODE_COLUMN_FORM_TYPE)
    private String formType;

    @ApiModelProperty(value = "查询 1:模糊 2：精确")
    @TableField(value = AdminFields.CODE_COLUMN_QUERY_TYPE)
    private String queryType;

    @ApiModelProperty(value = "字典名称")
    @TableField(value = AdminFields.CODE_COLUMN_DICT_NAME)
    private String dictName;

    @ApiModelProperty(value = "日期注解")
    @TableField(value = AdminFields.CODE_COLUMN_DATE_ANNOTATION)
    private String dateAnnotation;

    public ColumnInfoModel(String tableName, String columnName, Boolean notNull, String columnType, String remark,
                           String keyType, String extra) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.keyType = keyType;
        this.extra = extra;
        this.notNull = notNull;
        if (GenUtil.PK.equalsIgnoreCase(keyType) && GenUtil.EXTRA.equalsIgnoreCase(extra)) {
            this.notNull = false;
        }
        this.remark = remark;
        this.listShow = true;
        this.formShow = true;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
