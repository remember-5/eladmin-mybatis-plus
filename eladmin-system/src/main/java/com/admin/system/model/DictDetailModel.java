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
package com.admin.system.model;

import com.admin.base.BaseModel;
import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_DICT_DETAIL)
public class DictDetailModel extends BaseModel<DictDetailModel> implements Serializable {
    private static final long serialVersionUID = 4711939729536723488L;

    @ApiModelProperty(hidden = true)
    @TableId(value = AdminFields.DICT_DETAIL_DETAIL_ID, type = IdType.AUTO)
    @NotNull(groups = Update.class)
    private Long id;

    @TableField(value = AdminFields.DICT_DETAIL_DICT_ID)
    private Long dictId;

    @ApiModelProperty(value = "字典", hidden = true)
    @TableField(exist = false)
    private DictModel dict;

    @ApiModelProperty(value = "字典标签")
    private String label;

    @ApiModelProperty(value = "字典值")
    private String value;

    @ApiModelProperty(value = "排序")
    @TableField(value = AdminFields.DICT_DETAIL_DICT_SORT)
    private Integer dictSort = 999;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
