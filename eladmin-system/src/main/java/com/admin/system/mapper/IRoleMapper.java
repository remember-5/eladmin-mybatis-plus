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

import com.admin.system.model.RoleModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Mapper
public interface IRoleMapper extends BaseMapper<RoleModel> {

    @Select("SELECT r.role_id as id, r.* FROM sys_role r LEFT OUTER JOIN sys_users_roles ur ON r.role_id=ur.role_id LEFT OUTER JOIN sys_user u ON ur.user_id=u.user_id WHERE u.user_id=#{userId}")
    Set<RoleModel> selectLink(Long userId);

    @Select("DELETE FROM sys_roles_menus WHERE menu_id = #{menuId}")
    int deleteRolesByMenuId(Long menuId);

    /**
     * 根据用户ID查询
     *
     * @param id 用户ID
     * @return /
     */
    @Select("SELECT r.* FROM sys_role r, sys_users_roles u WHERE " + "r.role_id = u.role_id AND u.user_id = #{id}")
    Set<RoleModel> findByUserId(@Param("id") Long id);

    /**
     * 根据部门查询
     *
     * @param deptIds /
     * @return /
     */
    @Select("<script>select count(1) from sys_role r, sys_roles_depts d where "
            + "r.role_id = d.role_id and d.dept_id in "
            + "<foreach item='item' index='index' collection='deptIds' open='(' separator=',' close=')'> #{item} </foreach>"
            + "</script>")
    int countByDepts(@Param("deptIds") Set<Long> deptIds);


    @Select("<script>SELECT r.* FROM sys_role r, sys_roles_menus m WHERE "
            + "r.role_id = m.role_id AND m.menu_id in "
            + "<foreach item='item' index='index' collection='menuIds' open='(' separator=',' close=')'> #{item} </foreach>"
            + "</script>")
    List<RoleModel> findInMenuId(@Param("menuIds") List<Long> menuIds);

}
