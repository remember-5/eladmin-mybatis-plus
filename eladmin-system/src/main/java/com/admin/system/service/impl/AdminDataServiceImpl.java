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

import com.admin.utils.enums.DataScopeEnum;
import com.admin.modules.system.service.dto.RoleSmallDto;
import com.admin.modules.system.service.dto.UserDto;
import com.admin.system.model.DeptModel;
import com.admin.system.service.IDataService;
import com.admin.system.service.IDeptService;
import com.admin.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月7日
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "data")
public class AdminDataServiceImpl implements IDataService {
    private final IRoleService roleService;

    private final IDeptService deptService;

    @Override
    @SuppressWarnings("incomplete-switch")
    @Cacheable(key = "'user:' + #p0.id")
    public List<Long> getDeptIds(UserDto user) {
        // 用于存储部门id
        Set<Long> deptIds = new HashSet<>();
        // 查询用户角色
        List<RoleSmallDto> roleSet = roleService.findByUsersId(user.getId());
        // 获取对应的部门ID
        for (RoleSmallDto role : roleSet) {
            DataScopeEnum dataScopeEnum = DataScopeEnum.find(role.getDataScope());
            switch (Objects.requireNonNull(dataScopeEnum)) {
                case THIS_LEVEL:
                    deptIds.add(user.getDept().getId());
                    break;
                case CUSTOMIZE:
                    deptIds.addAll(getCustomize(deptIds, role));
                    break;
                default:
                    return new ArrayList<>(deptIds);
            }
        }
        return new ArrayList<>(deptIds);
    }

    /**
     * 获取自定义的数据权限
     *
     * @param deptIds 部门ID
     * @param role    角色
     * @return 数据权限ID
     */
    public Set<Long> getCustomize(Set<Long> deptIds, RoleSmallDto role) {
        Set<DeptModel> depts = deptService.findByRoleId(role.getId());
        for (DeptModel dept : depts) {
            deptIds.add(dept.getId());
            List<DeptModel> deptChildren = deptService.findByPid(dept.getId());
            if (deptChildren != null && deptChildren.size() != 0) {
                deptIds.addAll(deptService.getDeptChildren(dept.getId(), deptChildren));
            }
        }
        return deptIds;
    }

    /**
     * 递归获取子级部门
     *
     * @param deptList 部门
     * @return 数据权限
     */
    @Cacheable
    @Override
    public List<Long> getDeptChildren(List<DeptModel> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
            if (dept != null && dept.getEnabled()) {
                List<DeptModel> depts = deptService.findByPid(dept.getId());
                if (deptList.size() != 0) {
                    list.addAll(getDeptChildren(depts));
                }
                list.add(dept.getId());
            }
        });
        return list;
    }
}
