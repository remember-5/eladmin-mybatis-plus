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

import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 角色菜单关联
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_ROLES_MENUS)
@JsonIgnoreProperties(value = {"handler"})
@ApiModel(value = "RolesMenus对象", description = "角色菜单关联")
public class RolesMenusModel implements Serializable {
    private static final long serialVersionUID = 4099432108788310065L;

    @ApiModelProperty(value = "菜单ID")
    @TableField(value = AdminFields.ROLESMENUS_MENU_ID)
    private Long menuId;

    @ApiModelProperty(value = "角色ID")
    @TableField(value = AdminFields.ROLESMENUS_ROLE_ID)
    private Long roleId;
}
