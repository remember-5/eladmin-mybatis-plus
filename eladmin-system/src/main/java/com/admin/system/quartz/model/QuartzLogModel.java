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
package com.admin.system.quartz.model;

import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.*;
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
@TableName(AdminFields.TABLE_QUARTZ_LOG)
public class QuartzLogModel extends Model<QuartzLogModel> implements Serializable {
    private static final long serialVersionUID = 4778678607459723918L;

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = AdminFields.QUARTZ_LOG_LOG_ID, type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务名称", hidden = true)
    @TableField(value = AdminFields.QUARTZ_LOG_JOB_NAME)
    private String jobName;

    @ApiModelProperty(value = "bean名称", hidden = true)
    @TableField(value = AdminFields.QUARTZ_LOG_BEAN_NAME)
    private String beanName;

    @ApiModelProperty(value = "方法名称", hidden = true)
    @TableField(value = AdminFields.QUARTZ_LOG_METHOD_NAME)
    private String methodName;

    @ApiModelProperty(value = "参数", hidden = true)
    private String params;

    @ApiModelProperty(value = "cron表达式", hidden = true)
    @TableField(value = AdminFields.QUARTZ_LOG_CRON_EXPRESSION)
    private String cronExpression;

    @ApiModelProperty(value = "状态", hidden = true)
    @TableField(value = AdminFields.QUARTZ_LOG_IS_SUCCESS)
    private Boolean isSuccess;

    @ApiModelProperty(value = "异常详情", hidden = true)
    @TableField(value = AdminFields.QUARTZ_LOG_EXCEPTION_DETAIL)
    private String exceptionDetail;

    @ApiModelProperty(value = "执行耗时", hidden = true)
    private Long time;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = AdminFields.CREATE_TIME, fill = FieldFill.INSERT)
    private Timestamp createTime;
}
