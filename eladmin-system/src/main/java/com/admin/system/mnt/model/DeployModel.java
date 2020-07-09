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
package com.admin.system.mnt.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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

import java.io.Serializable;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_MNT_DEPLOY)
public class DeployModel extends BaseModel<DeployModel> implements Serializable {
    private static final long serialVersionUID = 4974593154876277478L;

    /**
     * 部署编号
     */
    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.MNT_DEPLOY_DEPLOY_ID, type = IdType.AUTO)
    private Long id;

    /**
     * 应用编号
     */
    @TableField(value = AdminFields.MNT_DEPLOY_APP_ID)
    private Long appId;

    @ApiModelProperty(value = "应用编号")
    @TableField(exist = false)
    private AppModel app;

    /**
     * 服务器
     */
    @ApiModelProperty(name = "服务器", hidden = true)
    @TableField(exist = false)
    private Set<ServerDeployModel> deploys;

    public void copy(DeployModel source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
