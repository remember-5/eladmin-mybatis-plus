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

/**
 * @author adyfang
 * @date 2020年5月5日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_MNT_APP)
public class AppModel extends BaseModel<AppModel> implements Serializable {
    private static final long serialVersionUID = 1275999710827435779L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.MNT_APP_APP_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "端口")
    private int port;

    @ApiModelProperty(value = "上传路径")
    @TableField(value = AdminFields.MNT_APP_UPLOAD_PATH)
    private String uploadPath;

    @ApiModelProperty(value = "部署路径")
    @TableField(value = AdminFields.MNT_APP_DEPLOY_PATH)
    private String deployPath;

    @ApiModelProperty(value = "备份路径")
    @TableField(value = AdminFields.MNT_APP_BACKUP_PATH)
    private String backupPath;

    @ApiModelProperty(value = "启动脚本")
    @TableField(value = AdminFields.MNT_APP_START_SCRIPT)
    private String startScript;

    @ApiModelProperty(value = "部署脚本")
    @TableField(value = AdminFields.MNT_APP_DEPLOY_SCRIPT)
    private String deployScript;

    public void copy(AppModel source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
