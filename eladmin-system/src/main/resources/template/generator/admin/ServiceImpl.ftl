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
package ${package}.service.impl;

import ${package}.model.${className};
<#if columns??>
    <#list columns as column>
        <#if column.columnKey = 'UNI'>
            <#if column_index = 1>
                import com.admin.exception.EntityExistException;
            </#if>
        </#if>
    </#list>
</#if>
import com.admin.utils.ValidationUtil;
import com.admin.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package}.service.${className}Service;
import ${package}.service.dto.${className}Dto;
import ${package}.service.dto.${className}QueryCriteria;
import ${package}.mapper.I${className}Mapper;
import com.admin.utils.DozerUtils;
import org.dozer.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<#if !auto && pkColumnType = 'Long'>
    import cn.hutool.core.lang.Snowflake;
    import cn.hutool.core.util.IdUtil;
</#if>
<#if !auto && pkColumnType = 'String'>
    import cn.hutool.core.util.IdUtil;
</#if>
import com.admin.utils.PageUtil;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author ${author}
* @date ${date}
**/
@Service
@RequiredArgsConstructor
public class ${className}ServiceImpl extends ServiceImpl
<I${className}Mapper, ${className}Model> implements I${className}Service {

// dozer做Model与DTO的转换
private final Mapper mapper;

@Override
public Map
<String, Object> queryAll(${className}QueryCriteria criteria, Pageable pageable){
IPage
<${className}Model> page = this.page(pageable, buildWrapper(criteria));
    List
    <${className}Dto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), ${className}Dto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
        }

        private QueryWrapper
        <${className}Model> buildWrapper(${className}QueryCriteria criteria) {
            QueryWrapper
            <${className}Model> query = null;
                if (null != criteria) {
                query = new QueryWrapper
                <${className}Model>();
                    }
                    return query;
                    }

                    @Override
                    public List
                    <${className}Dto> queryAll(${className}QueryCriteria criteria){
                        List
                        <${className}Model> ${changeClassName}s = this.list(buildWrapper(criteria));
                            return DozerUtils.mapList(mapper, ${changeClassName}s, ${className}Dto.class);
                            }

                            @Override
                            @Transactional
                            public ${className}Dto findById(${pkColumnType} ${pkChangeColName}) {
                            ${className}Model ${changeClassName} = Optional.ofNullable(this.getById(${pkChangeColName}
                            )).orElseGet(${className}Model::new);
                            ValidationUtil.isNull(${changeClassName}.getId(),"${className}","${pkChangeColName}
                            ",${pkChangeColName});
                            return mapper.map(${changeClassName}, ${className}Dto.class);
                            }

                            @Override
                            @Transactional(rollbackFor = Exception.class)
                            public ${className}Dto create(${className}Model resources) {
                            <#if !auto && pkColumnType = 'Long'>
                                Snowflake snowflake = IdUtil.createSnowflake(1, 1);
                                resources.set${pkCapitalColName}(snowflake.nextId());
                            </#if>
                            <#if !auto && pkColumnType = 'String'>
                                resources.set${pkCapitalColName}(IdUtil.simpleUUID());
                            </#if>
                            <#if columns??>
                            <#list columns as column>
                            <#if column.columnKey = 'UNI'>
                            QueryWrapper
                            <${className}Model> query = new QueryWrapper
                                <${className}Model>();
                                    query.lambda().eq(${className}Model::get${column.capitalColumnName},
                                    resources.get${column.capitalColumnName}());
                                    if(this.getOne(query) != null){
                                    throw new EntityExistException(${className}Model.class, "${column.columnName}",
                                    resources.get${column.capitalColumnName}());
                                    }
                                    </#if>
                                    </#list>
                                    </#if>
                                    ${changeClassName}Mapper.toDto(this.save(resources))
                                    return resources;
                                    }

                                    @Override
                                    @Transactional(rollbackFor = Exception.class)
                                    public void update(${className}Model resources) {
                                    ${className}Model ${changeClassName} =
                                    Optional.ofNullable(this.getById(resources.getId())).orElseGet(${className}
                                    Model::new);
                                    ValidationUtil.isNull(${changeClassName}.getId(), "${className}", "id",
                                    resources.getId());
                                    <#if columns??>
                                    <#list columns as column>
                                    <#if column.columnKey = 'UNI'>
                                    <#if column_index = 1>
                                        ${className}Model ${changeClassName}1 = null;
                                    </#if>
                                    QueryWrapper
                                    <${className}Model> query = new QueryWrapper
                                        <${className}Model>();
                                            query.lambda().eq(${className}Model::get${column.capitalColumnName},
                                            resources.get${column.capitalColumnName}());
                                            ${changeClassName}1 = this.getOne(query);
                                            if(${changeClassName}1 != null && !${changeClassName}
                                            1.get${pkCapitalColName}().equals(${changeClassName}.get${pkCapitalColName}
                                            ())){
                                            throw new EntityExistException(${className}Model.class,
                                            "${column.columnName}", resources.get${column.capitalColumnName}());
                                            }
                                            </#if>
                                            </#list>
                                            </#if>
                                            ${changeClassName} = mapper.map(resources, ${className}Model.class);
                                            this.saveOrUpdate(${changeClassName});
                                            }

                                            @Override
                                            public void deleteAll(${pkColumnType}[] ids) {
                                            this.removeByIds(ids);
                                            }

                                            @Override
                                            public void download(List
                                            <${className}Dto> all, HttpServletResponse response) throws IOException {
                                                List
                                                <Map
                                                <String
                                                , Object>> list = new ArrayList<>();
                                                for (${className}Dto ${changeClassName} : all) {
                                                Map
                                                <String
                                                ,Object> map = new LinkedHashMap<>();
                                                <#list columns as column>
                                                    <#if column.columnKey != 'PRI'>
                                                        <#if column.remark != ''>
                                                            map.put("${column.remark}", ${changeClassName}.get${column.capitalColumnName}());
                                                        <#else>
                                                            map.put(" ${column.changeColumnName}",  ${changeClassName}.get${column.capitalColumnName}());
                                                        </#if>
                                                    </#if>
                                                </#list>
                                                list.add(map);
                                                }
                                                FileUtil.downloadExcel(list, response);
                                                }
                                                }