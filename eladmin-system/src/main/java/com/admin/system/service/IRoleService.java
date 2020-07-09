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
package com.admin.system.service;

import com.admin.modules.system.service.dto.RoleDto;
import com.admin.modules.system.service.dto.RoleQueryCriteria;
import com.admin.modules.system.service.dto.RoleSmallDto;
import com.admin.modules.system.service.dto.UserDto;
import com.admin.system.model.RoleModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年4月26日
 */
public interface IRoleService extends IService<RoleModel> {

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    RoleDto findById(long id);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(RoleModel resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(RoleModel resources);

    /**
     * 删除
     *
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 根据用户ID查询
     *
     * @param id 用户ID
     * @return /
     */
    List<RoleSmallDto> findByUsersId(Long id);

    /**
     * 根据角色查询角色级别
     *
     * @param roles /
     * @return /
     */
    Integer findByRoles(Set<RoleModel> roles);

    /**
     * 修改绑定的菜单
     *
     * @param resources /
     * @param roleDTO   /
     */
    void updateMenu(RoleModel resources, RoleDto roleDTO);

    /**
     * 解绑菜单
     *
     * @param id /
     */
    void untiedMenu(Long id);

    /**
     * 不带条件分页查询
     *
     * @return /
     */
    Object queryAll();

    /**
     * 待条件分页查询
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    @SuppressWarnings("rawtypes")
    Object queryAll(RoleQueryCriteria criteria, IPage pageable);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @return /
     */
    List<RoleDto> queryAll(RoleQueryCriteria criteria);

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<RoleDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 获取用户权限信息
     *
     * @param user 用户信息
     * @return 权限信息
     */
    List<GrantedAuthority> mapToGrantedAuthorities(UserDto user);

    /**
     * @param ids
     */
    void verification(Set<Long> ids);


    List<RoleModel> findInMenuId(List<Long> menuIds);

}
