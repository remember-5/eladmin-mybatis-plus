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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * @author adyfang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_DEPT)
@ApiModel(value = "Dept对象", description = "部门")
public class DeptModel extends BaseModel<DeptModel> implements Serializable {

    private static final long serialVersionUID = 8187165962228631644L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.DEPT_DEPT_ID, type = IdType.AUTO)
    @NotNull(groups = Update.class)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "部门名称")
    @EqualsAndHashCode.Include
    private String name;

    @NotNull
    @ApiModelProperty(value = "上级部门")
    private Long pid;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "排序")
    @TableField(value = AdminFields.DEPT_DEPT_SORT)
    private Integer deptSort;

    @ApiModelProperty(value = "子节点数目", hidden = true)
    @TableField(value = AdminFields.DEPT_SUB_COUNT)
    private Integer subCount = 0;

    @JsonIgnore
    @ApiModelProperty(value = "角色")
    @TableField(exist = false)
    private Set<RoleModel> roles;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
