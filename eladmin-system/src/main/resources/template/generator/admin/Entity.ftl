/*
*  Copyright 2019-2020 ${author}
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
package ${package}.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
<#if isNotNullColumns??>
    import javax.validation.constraints.*;
</#if>
<#if hasDateAnnotation>
</#if>
<#if hasTimestamp>
    import java.sql.Timestamp;
</#if>
<#if hasBigDecimal>
    import java.math.BigDecimal;
</#if>
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description ${tableName}
* @author ${author}
* @date ${date}
**/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(value = { "handler" })
@TableName("${tableName}")
public class ${className}Model extends Model
<${className}Model> implements Serializable {
    <#if columns??>
        <#list columns as column>

            <#if column.columnKey = 'PRI'>
                <#if auto>
                    @TableId(value = ${column.columnName}, type = IdType.AUTO)
                <#else>
                    @TableId(value = ${column.columnName}, type = IdType.NONE)
                </#if>
            </#if>
            @TableField(value = "${column.columnName}"<#if (column.dateAnnotation)??><#if column.dateAnnotation = 'CreationTimestamp'>, fill = FieldFill.INSERT<#else>, fill = FieldFill.UPDATE</#if></#if>)
            <#if column.istNotNull && column.columnKey != 'PRI'>
                <#if column.columnType = 'String'>
                    @NotBlank
                <#else>
                    @NotNull
                </#if>
            </#if>
            <#if column.remark != ''>
                @ApiModelProperty(value = "${column.remark}")
            <#else>
                @ApiModelProperty(value = "${column.changeColumnName}")
            </#if>
            private ${column.columnType} <#if column.columnKey = 'PRI'>id<#else>${column.changeColumnName}</#if>;
        </#list>
    </#if>

    @Override
    protected Serializable pkVal() {
    return id;
    }
    }