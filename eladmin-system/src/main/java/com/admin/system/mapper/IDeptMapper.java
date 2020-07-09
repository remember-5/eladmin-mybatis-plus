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

import com.admin.system.model.DeptModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * @author adyfang
 */
@Mapper
public interface IDeptMapper extends BaseMapper<DeptModel> {

    /**
     * ~
     *
     * @param id /
     * @return /
     */
    @Select("select d.dept_id as id, d.* from sys_dept d where dept_id = #{id}")
    DeptModel selectLink(Long id);

    /**
     * ~
     *
     * @param roleId /
     * @return /
     */
    @Select("SELECT d.dept_id as id, d.* FROM sys_dept d LEFT OUTER JOIN sys_roles_depts rd ON d.dept_id=rd.dept_id WHERE rd.role_id=#{roleId}")
    Set<DeptModel> selectByRoleId(Long roleId);
}
