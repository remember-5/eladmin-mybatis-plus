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
 * <p>
 * 系统菜单
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(AdminFields.TABLE_MENU)
@JsonIgnoreProperties(value = {"handler"})
@ApiModel(value = "Menu对象", description = "系统菜单")
public class MenuModel extends BaseModel<MenuModel> implements Serializable {
    private static final long serialVersionUID = 2128395305544589220L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.MENU_MENU_ID, type = IdType.AUTO)
    @NotNull(groups = {Update.class})
    private Long id;

    @JsonIgnore
    @ApiModelProperty(value = "菜单角色")
    @TableField(exist = false)
    private Set<RoleModel> roles;

    @NotBlank
    @ApiModelProperty(value = "菜单标题")
    private String title;

    @ApiModelProperty(value = "组件名称")
    @TableField(value = AdminFields.MENU_NAME)
    private String componentName;

    @ApiModelProperty(value = "排序")
    @TableField(value = AdminFields.MENU_MENU_SORT)
    private Integer menuSort = 999;

    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "菜单类型，目录、菜单、按钮")
    private Integer type;

    @ApiModelProperty(value = "权限")
    private String permission;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "缓存")
    private Boolean cache;

    @ApiModelProperty(value = "隐藏")
    private Boolean hidden;

    @ApiModelProperty(value = "上级菜单ID")
    private Long pid;

    @ApiModelProperty(value = "外链菜单")
    @TableField(value = AdminFields.MENU_I_FRAME)
    private Boolean iFrame;

    @ApiModelProperty(value = "子节点数目", hidden = true)
    @TableField(value = AdminFields.MENU_SUB_COUNT)
    private Integer subCount = 0;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
