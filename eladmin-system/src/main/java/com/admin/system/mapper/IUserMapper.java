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

import com.admin.system.model.UserModel;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * @author adyfang
 */
@Mapper
public interface IUserMapper extends BaseMapper<UserModel> {
    /**
     * ${ew.customSqlSegment}” （自定义sql段），wrapper不能为null
     */
    @Results({
            @Result(column = "dept_id", property = "dept", one = @One(select = "com.admin.system.mapper.IDeptMapper.selectLink")),
            @Result(column = "job_id", property = "job", one = @One(select = "com.admin.system.mapper.IJobMapper.selectLink")),})
    @Select("select u.user_id as id, u.* from sys_user u ${ew.customSqlSegment}")
    UserModel selectLink(@Param(Constants.WRAPPER) Wrapper<UserModel> query);

    /**
     * 根据角色查询用户
     *
     * @param roleId /
     * @return /
     */
    @Select("SELECT u.* FROM sys_user u, sys_users_roles r WHERE" + " u.user_id = r.user_id AND r.role_id = #{roleId}")
    List<UserModel> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色中的部门查询
     *
     * @param id /
     * @return /
     */
    @Select("SELECT u.* FROM sys_user u, sys_users_roles r, sys_roles_depts d WHERE "
            + "u.user_id = r.user_id AND r.role_id = d.role_id AND r.role_id = #{roleId} group by u.user_id")
    List<UserModel> findByDeptRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单查询
     *
     * @param id 菜单ID
     * @return /
     */
    @Select("SELECT u.* FROM sys_user u, sys_users_roles ur, sys_roles_menus rm WHERE"
            + "u.user_id = ur.user_id AND ur.role_id = rm.role_id AND rm.menu_id = #{menuId} group by u.user_id")
    List<UserModel> findByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据岗位查询
     *
     * @param ids /
     * @return /
     */
    @Select("<script>SELECT count(1) FROM sys_user u, sys_users_jobs j WHERE u.user_id = j.user_id AND j.job_id IN "
            + "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> #{item} </foreach>"
            + "</script>")
    int countByJobs(@Param("ids") Set<Long> ids);

    /**
     * 根据部门查询
     *
     * @param deptIds /
     * @return /
     */
    @Select("<script>SELECT count(1) FROM sys_user u WHERE u.dept_id IN "
            + "<foreach item='item' index='index' collection='deptIds' open='(' separator=',' close=')'> #{item} </foreach>"
            + "</script>")
    int countByDepts(@Param("deptIds") Set<Long> deptIds);

    /**
     * 根据角色查询
     *
     * @return /
     */
    @Select("<script>SELECT count(1) FROM sys_user u, sys_users_roles r WHERE "
            + "u.user_id = r.user_id AND r.role_id in "
            + "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> #{item} </foreach>"
            + "</script>")
    int countByRoles(@Param("ids") Set<Long> ids);
}
