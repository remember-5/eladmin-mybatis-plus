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
import com.admin.enums.DataScopeEnum;
import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * <p>
 * 角色表
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_ROLE)
@JsonIgnoreProperties(value = {"handler"})
@ApiModel(value = "Role对象", description = "角色表")
public class RoleModel extends BaseModel<RoleModel> implements Serializable {
    private static final long serialVersionUID = -3251302234200933444L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.ROLE_ROLE_ID, type = IdType.AUTO)
    @NotNull(groups = {Update.class})
    @EqualsAndHashCode.Include
    private Long id;

    @ApiModelProperty(value = "名称", hidden = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "数据权限，全部 、 本级 、 自定义")
    @TableField(value = AdminFields.ROLE_DATA_SCOPE)
    private String dataScope = DataScopeEnum.THIS_LEVEL.getValue();

    @ApiModelProperty(value = "级别，数值越小，级别越大")
    private Integer level = 3;

    @ApiModelProperty(value = "描述")
    private String description;

    @TableField(exist = false)
    private Set<DeptModel> depts;

    @TableField(exist = false)
    private Set<MenuModel> menus;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
