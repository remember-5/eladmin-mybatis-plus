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

import com.admin.system.model.MenuModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 * 系统菜单 Mapper 接口
 * </p>
 *
 * @author adyfang
 * @since 2020-04-25
 */
@Mapper
public interface IMenuMapper extends BaseMapper<MenuModel> {
    @Select("SELECT m.menu_id as id, m.* FROM sys_roles_menus rm INNER JOIN sys_menu m ON rm.menu_id=m.menu_id WHERE rm.role_id=#{roleId}")
    Set<MenuModel> selectLink(Long roleId);

    @Select({"<script>SELECT m.menu_id as id, m.* FROM sys_roles_menus rm INNER JOIN sys_menu m ON rm.menu_id=m.menu_id WHERE rm.role_id IN"
            + "<foreach item='item' index='index' collection='roleIds' open='(' separator=',' close=')'> #{item} </foreach>"
            + "</script>"})
    Set<MenuModel> selectByRoleIds(@Param("roleIds") Set<Long> roleIds);

    @Select({"<script>SELECT m.menu_id as id, m.* FROM sys_menu m LEFT OUTER JOIN sys_roles_menus rm ON m.menu_id=rm.menu_id LEFT OUTER JOIN sys_role r ON rm.role_id=r.role_id WHERE r.role_id IN "
            + "<foreach item='item' index='index' collection='roleIds' open='(' separator=',' close=')'> #{item} </foreach>"
            + " AND m.type &lt;&gt; #{type} ORDER BY m.menu_sort ASC</script>"})
    LinkedHashSet<MenuModel> selectLinkRole(@Param("roleIds") Set<Long> roleIds, @Param("type") Long type);
}
