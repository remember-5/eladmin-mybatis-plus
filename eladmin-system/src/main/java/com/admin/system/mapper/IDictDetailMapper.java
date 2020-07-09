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
package com.admin.system.mapper;

import com.admin.modules.system.service.dto.DictDetailQueryCriteria;
import com.admin.system.model.DictDetailModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
public interface IDictDetailMapper extends BaseMapper<DictDetailModel> {
    //  @Select("SELECT dt.* FROM dict_detail dt LEFT OUTER JOIN dict d ON dt.dict_id=d.id ${ew.customSqlSegment}")
    @Select({"<script>"
            + "SELECT dt.dict_id as id, dt.* FROM sys_dict_detail dt "
            + "LEFT OUTER JOIN sys_dict d ON dt.dict_id=d.dict_id "
            + "<where>"
            + "  <if test='criteria != null and criteria.label != null'>"
            + "    AND dt.label LIKE CONCAT('%', #{criteria.label}, '%') "
            + "  </if>"
            + "  <if test='criteria != null and criteria.dictName != null'>"
            + "    AND d.name=#{criteria.dictName} "
            + "  </if>"
            + "</where>"
            + "</script>"})
    List<DictDetailModel> selectJoin(@Param("page") IPage<DictDetailModel> page, @Param("criteria") DictDetailQueryCriteria criteria);

    /**
     * 根据字典名称查询
     *
     * @param name /
     * @return /
     */
    @Select("select dd.* from sys_dict_detail dd left join sys_dict d on d.dict_id=dd.dict_id where d.name = #{name}")
    List<DictDetailModel> findByDictName(@Param("name") String name);
}
