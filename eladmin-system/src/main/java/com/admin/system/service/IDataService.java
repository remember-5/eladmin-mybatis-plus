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

import com.admin.modules.system.service.dto.UserDto;
import com.admin.system.model.DeptModel;

import java.util.List;

/**
 * @author adyfang
 * @date 2020年5月7日
 */
public interface IDataService {
    /**
     * 获取数据权限
     *
     * @param user /
     * @return /
     */
    List<Long> getDeptIds(UserDto user);

    /**
     * 递归获取子级部门
     *
     * @param deptList /
     * @return /
     */
    List<Long> getDeptChildren(List<DeptModel> deptList);
}
