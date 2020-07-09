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
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_USER)
public class UserModel extends BaseModel<UserModel> implements Serializable {

    private static final long serialVersionUID = 3831089352245272160L;

    @ApiModelProperty(hidden = true)
    @EqualsAndHashCode.Include
    @TableId(value = AdminFields.USER_USER_ID, type = IdType.AUTO)
    @NotNull(groups = Update.class)
    private Long id;

    @NotBlank
    @EqualsAndHashCode.Include
    @TableField(value = AdminFields.USER_USERNAME)
    @ApiModelProperty(value = "用户名称")
    private String username;

    @NotBlank
    @TableField(value = AdminFields.USER_NICK_NAME)
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @NotBlank
    @ApiModelProperty(value = "邮箱")
    private String email;

    @NotBlank
    @ApiModelProperty(value = "电话号码")
    private String phone;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "头像真实名称", hidden = true)
    @TableField(value = AdminFields.USER_AVATAR_NAME)
    private String avatarName;

    @ApiModelProperty(value = "头像存储的路径", hidden = true)
    @TableField(value = AdminFields.USER_AVATAR_PATH)
    private String avatarPath;

    @ApiModelProperty(value = "密码")
    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @TableField(value = AdminFields.USER_PWD_RESET_TIME)
    @ApiModelProperty(value = "最后修改密码的时间", hidden = true)
    private Date pwdResetTime;

    @ApiModelProperty(value = "是否为admin账号", hidden = true)
    @TableField(value = AdminFields.USER_IS_ADMIN)
    private Boolean isAdmin = false;

    @ApiModelProperty(value = "用户角色")
    @TableField(exist = false)
    private Set<RoleModel> roles;

    @ApiModelProperty(value = "用户岗位")
    @TableField(exist = false)
    private Set<JobModel> jobs;

    @TableField(value = AdminFields.USER_DEPT_ID)
    @ApiModelProperty(value = "用户部门")
    private Long deptId;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户部门")
    private DeptModel dept;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
