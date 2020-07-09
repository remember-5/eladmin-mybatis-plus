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
package com.admin.base;

import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author adyfang
 * @date 2020年5月7日
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
public class BaseModel<T extends Model<?>> extends Model<T> {

    @ApiModelProperty(value = "操作人")
    @TableField(value = AdminFields.CREATE_BY, fill = FieldFill.INSERT)
    private String createBy;

    @TableField(value = AdminFields.CREATE_TIME, fill = FieldFill.INSERT)
    private Timestamp createTime;

    @ApiModelProperty(value = "更新人", hidden = true)
    @TableField(value = AdminFields.UPDATE_BY, fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @TableField(value = AdminFields.UPDATE_TIME, fill = FieldFill.UPDATE)
    private Timestamp updateTime;

    /* 分组校验 */
    public @interface Update {
    }
}
