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
import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_MNT_DEPLOY_HISTORY)
public class DeployHistoryModel extends Model<DeployHistoryModel> implements Serializable {
    private static final long serialVersionUID = 8870413882444926656L;

    @ApiModelProperty(hidden = true)
    @TableId(value = AdminFields.MNT_DEPLOY_HISTORY_HISTORY_ID, type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "应用名称")
    @TableField(value = AdminFields.MNT_DEPLOY_HISTORY_APP_NAME)
    private String appName;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "部署时间")
    @TableField(value = AdminFields.MNT_DEPLOY_HISTORY_DEPLOY_DATE)
    private Timestamp deployDate;

    @ApiModelProperty(value = "部署者")
    @TableField(value = AdminFields.MNT_DEPLOY_HISTORY_DEPLOY_USER)
    private String deployUser;

    @ApiModelProperty(value = "部署ID")
    @TableField(value = AdminFields.MNT_DEPLOY_HISTORY_DEPLOY_ID)
    private Long deployId;

    public void copy(DeployHistoryModel source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
