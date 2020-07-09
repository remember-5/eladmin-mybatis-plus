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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = {"handler"})
@TableName(AdminFields.TABLE_QUARTZ_JOB)
public class QuartzJobModel extends BaseModel<QuartzJobModel> implements Serializable {
    private static final long serialVersionUID = 4367385905148165547L;

    public static final String JOB_KEY = "JOB_KEY";

    @ApiModelProperty(hidden = true)
    @TableId(value = AdminFields.QUARTZ_JOB_JOB_ID, type = IdType.AUTO)
    @NotNull(groups = {Update.class})
    private Long id;

    @ApiModelProperty(value = "用于子任务唯一标识", hidden = true)
    @TableField(exist = false)
    private String uuid;

    @ApiModelProperty(value = "定时器名称")
    @TableField(value = AdminFields.QUARTZ_JOB_JOB_NAME)
    private String jobName;

    @ApiModelProperty(value = "Bean名称")
    @TableField(value = AdminFields.QUARTZ_JOB_BEAN_NAME)
    private String beanName;

    @ApiModelProperty(value = "方法名称")
    @TableField(value = AdminFields.QUARTZ_JOB_METHOD_NAME)
    private String methodName;

    @ApiModelProperty(value = "参数")
    private String params;

    @ApiModelProperty(value = "cron表达式")
    @TableField(value = AdminFields.QUARTZ_JOB_CRON_EXPRESSION)
    @NotBlank
    private String cronExpression;

    @ApiModelProperty(value = "状态，暂时或启动")
    @TableField(value = AdminFields.QUARTZ_JOB_IS_PAUSE)
    private Boolean isPause = false;

    @ApiModelProperty(value = "负责人")
    @TableField(value = AdminFields.QUARTZ_JOB_PERSON_IN_CHARGE)
    private String personInCharge;

    @ApiModelProperty(value = "报警邮箱")
    private String email;

    @ApiModelProperty(value = "子任务")
    @TableField(value = AdminFields.QUARTZ_JOB_SUB_TASK)
    private String subTask;

    @ApiModelProperty(value = "失败后暂停")
    @TableField(value = AdminFields.QUARTZ_JOB_PAUSE_AFTER_FAILURE)
    private Boolean pauseAfterFailure;

    @NotBlank
    @ApiModelProperty(value = "备注")
    private String description;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
