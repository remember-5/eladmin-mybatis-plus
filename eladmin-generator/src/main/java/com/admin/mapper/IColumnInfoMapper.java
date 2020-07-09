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
package com.admin.mapper;

import com.admin.model.ColumnInfoModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author adyfang
 * @since 2020-05-30
 */
@Mapper
public interface IColumnInfoMapper extends BaseMapper<ColumnInfoModel> {

    @Select("select table_name ,create_time , engine, table_collation, table_comment from information_schema.tables "
            + "where table_schema = (select database()) " + "order by create_time desc")
    Map<String, Object> getTables();

    @Select("select table_name ,create_time , engine, table_collation, table_comment from information_schema.tables "
            + "where table_schema = (select database()) "
            + "and table_name like '${tableName}' order by create_time desc limit #{start}, #{totalPer}")
    List<Map<String, Object>> getByTableName(@Param("tableName") String tableName, @Param("start") int start,
                                             @Param("totalPer") int totalPer);

    @Select("select column_name, is_nullable, data_type, column_comment, column_key, extra from information_schema.columns "
            + "where table_name = #{tableName} and table_schema = (select database()) order by ordinal_position")
    List<Map<String, Object>> getByTableNameOrderByPosition(@Param("tableName") String tableName);

    @Select("SELECT COUNT(*) from information_schema.tables where table_schema = (select database())")
    int countTables();
}
