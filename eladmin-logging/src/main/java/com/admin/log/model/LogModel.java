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
package com.admin.log.model;

import com.admin.utils.AdminFields;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author adyfang
 * @date 2020年5月3日
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(AdminFields.TABLE_LOG)
@JsonIgnoreProperties(value = {"handler"})
@ApiModel(value = "Log对象", description = "系统日志")
public class LogModel {
    @ApiModelProperty(hidden = true)
    @TableId(value = AdminFields.LOG_LOG_ID, type = IdType.AUTO)
    private Long id;

    private String username;

    private String description;

    private String method;

    private String params;

    @TableField(value = AdminFields.LOG_LOG_TYPE)
    private String logType;

    @TableField(value = AdminFields.LOG_REQUEST_IP)
    private String requestIp;

    private String address;

    private String browser;

    private Long time;

    @TableField(value = AdminFields.LOG_EXCEPTION_DETAIL)
    private byte[] exceptionDetail;

    @TableField(value = AdminFields.CREATE_TIME, fill = FieldFill.INSERT)
    private Timestamp createTime;

    public LogModel(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}
