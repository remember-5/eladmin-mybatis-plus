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
package com.admin.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.admin.exception.BadRequestException;
import com.admin.exception.EntityExistException;
import com.admin.modules.system.service.dto.RoleDto;
import com.admin.modules.system.service.dto.RoleQueryCriteria;
import com.admin.modules.system.service.dto.RoleSmallDto;
import com.admin.modules.system.service.dto.UserDto;
import com.admin.system.mapper.IMenuMapper;
import com.admin.system.mapper.IRoleMapper;
import com.admin.system.mapper.IUserMapper;
import com.admin.system.model.MenuModel;
import com.admin.system.model.RoleModel;
import com.admin.system.model.RolesMenusModel;
import com.admin.system.model.UserModel;
import com.admin.system.security.service.UserCacheClean;
import com.admin.system.service.IDeptService;
import com.admin.system.service.IRoleService;
import com.admin.system.service.IRolesMenusService;
import com.admin.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "role")
public class AdminRoleServiceImpl extends ServiceImpl<IRoleMapper, RoleModel> implements IRoleService {

    private final Mapper mapper;
    private final IMenuMapper menuMapper;
    private final IUserMapper userMapper;
    private final IRolesMenusService rolesMenusService;
    private final IDeptService deptService;
    private final RedisUtils redisUtils;
    private final UserCacheClean userCacheClean;

    @Override
    public List<RoleDto> queryAll() {
        QueryWrapper<RoleModel> query = new QueryWrapper<>();
        query.lambda().orderByAsc(RoleModel::getLevel);
        List<RoleModel> list = this.list(query);
        list.forEach(role -> {
            role.setMenus(menuMapper.selectLink(role.getId()));
            role.setDepts(deptService.findByRoleId(role.getId()));
        });
        return DozerUtils.mapList(mapper, list, RoleDto.class);
    }

    @Override
    public List<RoleDto> queryAll(RoleQueryCriteria criteria) {
        return DozerUtils.mapList(mapper, findAll(criteria), RoleDto.class);
    }

    public List<RoleModel> findAll(RoleQueryCriteria criteria) {
        return this.list(buildWrapper(criteria));
    }

    private QueryWrapper<RoleModel> buildWrapper(RoleQueryCriteria criteria) {
        QueryWrapper<RoleModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<>();
            query.lambda()
                    .nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                            i -> i.like(RoleModel::getName, criteria.getBlurry()).or().like(RoleModel::getDescription,
                                    criteria.getBlurry()))
                    .between(haveTime, RoleModel::getCreateTime, start, end);
        }
        return query;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(RoleQueryCriteria criteria, IPage pageable) {
        IPage<RoleModel> page = this.page(pageable, buildWrapper(criteria));
        page.getRecords().forEach(role -> {
            role.setMenus(menuMapper.selectLink(role.getId()));
            role.setDepts(deptService.findByRoleId(role.getId()));
        });
        page.convert(x -> mapper.map(x, RoleDto.class));
        return PageUtil.toPage(page.getRecords(), page.getTotal());
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public RoleDto findById(long id) {
        RoleModel role = Optional.ofNullable(this.getById(id)).orElseGet(RoleModel::new);
        role.setMenus(menuMapper.selectLink(role.getId()));
        role.setDepts(deptService.findByRoleId(role.getId()));
        ValidationUtil.isNull(role.getId(), "Role", "id", id);
        return mapper.map(role, RoleDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleModel resources) {
        QueryWrapper<RoleModel> query = new QueryWrapper<>();
        query.lambda().eq(RoleModel::getName, resources.getName());
        if (this.getOne(query) != null) {
            throw new EntityExistException(RoleModel.class, "username", resources.getName());
        }
        this.saveOrUpdate(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleModel resources) {
        RoleModel role = Optional.ofNullable(this.getById(resources.getId())).orElseGet(RoleModel::new);
        ValidationUtil.isNull(role.getId(), "Role", "id", resources.getId());

        QueryWrapper<RoleModel> query = new QueryWrapper<>();
        query.lambda().eq(RoleModel::getName, resources.getName());
        RoleModel role1 = this.getOne(query);

        if (role1 != null && !role1.getId().equals(role.getId())) {
            throw new EntityExistException(RoleModel.class, "username", resources.getName());
        }
        role.setName(resources.getName());
        role.setDescription(resources.getDescription());
        role.setDataScope(resources.getDataScope());
        role.setDepts(resources.getDepts()); // TODO role与dept貌似没关联关系
        role.setLevel(resources.getLevel());
        this.saveOrUpdate(role);
        // 更新相关缓存
        delCaches(role.getId(), null);
    }

    @Override
    public void updateMenu(RoleModel resources, RoleDto roleDTO) {
        RoleModel role = mapper.map(roleDTO, RoleModel.class);

        List<UserModel> users = userMapper.findByRoleId(role.getId());


        role.setMenus(resources.getMenus());
        delCaches(resources.getId(), users);
        this.saveOrUpdate(role);
        QueryWrapper<RolesMenusModel> query = new QueryWrapper<>();
        query.lambda().eq(RolesMenusModel::getRoleId, role.getId());
        RolesMenusModel rm = new RolesMenusModel();
        List<Long> oldMenuIds = rolesMenusService.list(query).stream().map(RolesMenusModel::getMenuId)
                .collect(Collectors.toList());
        List<Long> menuIds = role.getMenus().stream().map(MenuModel::getId).collect(Collectors.toList());
        List<Long> deleteList = oldMenuIds.stream().filter(item -> !menuIds.contains(item))
                .collect(Collectors.toList());
        List<Long> addList = menuIds.stream().filter(item -> !oldMenuIds.contains(item)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deleteList)) {
            query.lambda().in(RolesMenusModel::getMenuId, deleteList);
            rolesMenusService.remove(query);
        }
        addList.forEach(item -> {
            rm.setMenuId(item);
            rm.setRoleId(resources.getId());
            rolesMenusService.save(rm);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void untiedMenu(Long menuId) {
        baseMapper.deleteRolesByMenuId(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        baseMapper.deleteBatchIds(ids);
        for (Long id : ids) {
            // 更新相关缓存
            delCaches(id, null);
        }
    }

    @Override
    public List<RoleSmallDto> findByUsersId(Long id) {
        List<RoleModel> roles = new ArrayList<>();
        roles.addAll(baseMapper.selectLink(id));
        return DozerUtils.mapList(mapper, roles, RoleSmallDto.class);
    }

    @Override
    public Integer findByRoles(Set<RoleModel> roles) {
        Set<RoleDto> roleDtos = new HashSet<>();
        for (RoleModel role : roles) {
            roleDtos.add(findById(role.getId()));
        }
        return Collections.min(roleDtos.stream().map(RoleDto::getLevel).collect(Collectors.toList()));
    }

    @Override
    @Cacheable(key = "'auth:' + #p0.id")
    public List<GrantedAuthority> mapToGrantedAuthorities(UserDto user) {
        Set<String> permissions = new HashSet<>();
        // 如果是管理员直接返回
        if (user.getIsAdmin()) {
            permissions.add("admin");
            return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        Set<RoleModel> roles = baseMapper.selectLink(user.getId());
        Set<Long> roleIds = roles.stream().map(RoleModel::getId).collect(Collectors.toSet());
        QueryWrapper<RolesMenusModel> query = new QueryWrapper<>();
        query.lambda().in(RolesMenusModel::getRoleId, roleIds);
        Map<Long, List<RolesMenusModel>> roleMenuIds = rolesMenusService.list(query).stream()
                .collect(Collectors.groupingBy(RolesMenusModel::getRoleId));
        Set<Long> menuIds = roleMenuIds.values().stream().flatMap(item -> item.stream().map(RolesMenusModel::getMenuId))
                .collect(Collectors.toSet());
        List<MenuModel> menus = menuMapper.selectBatchIds(menuIds);
        permissions = menus.stream().filter(menu -> StringUtils.isNotBlank(menu.getPermission()))
                .map(MenuModel::getPermission).collect(Collectors.toSet());
        // permissions.addAll(roles.stream().flatMap(role ->
        // menuMapper.selectLink(role.getId()).stream())
        // .filter(menu ->
        // StringUtils.isNotBlank(menu.getPermission())).map(MenuModel::getPermission)
        // .collect(Collectors.toSet()));
        if (CollectionUtils.isNotEmpty(roleIds)) {
            permissions.addAll(menuMapper.selectByRoleIds(roleIds).stream()
                    .filter(menu -> StringUtils.isNotBlank(menu.getPermission())).map(MenuModel::getPermission)
                    .collect(Collectors.toSet()));
        }
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public void download(List<RoleDto> roles, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoleDto role : roles) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("角色名称", role.getName());
            map.put("角色级别", role.getLevel());
            map.put("描述", role.getDescription());
            map.put("创建日期", role.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void verification(Set<Long> ids) {
        if (userMapper.countByRoles(ids) > 0) {
            throw new BadRequestException("所选角色存在用户关联，请解除关联再试！");
        }
    }

    @Override
    public List<RoleModel> findInMenuId(List<Long> menuIds) {
        return baseMapper.findInMenuId(menuIds);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, List<UserModel> users) {
        users = CollectionUtil.isEmpty(users) ? userMapper.findByRoleId(id) : users;
        if (CollectionUtil.isNotEmpty(users)) {
            users.forEach(item -> userCacheClean.cleanUserCache(item.getUsername()));
            Set<Long> userIds = users.stream().map(UserModel::getId).collect(Collectors.toSet());
            redisUtils.delByKeys(CacheKey.DATE_USER, userIds);
            redisUtils.delByKeys(CacheKey.MENU_USER, userIds);
            redisUtils.delByKeys(CacheKey.ROLE_AUTH, userIds);
            redisUtils.del(CacheKey.ROLE_ID + id);
        }
    }


}
