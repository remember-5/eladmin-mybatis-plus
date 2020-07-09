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

import com.admin.modules.system.service.dto.DeptDto;
import com.admin.modules.system.service.dto.DeptQueryCriteria;
import com.admin.system.model.DeptModel;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年4月27日
 */
public interface IDeptService extends IService<DeptModel> {
    /**
     * 查询所有数据
     *
     * @param criteria 条件
     * @param isQuery  /
     * @return /
     * @throws Exception /
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception;

    List<DeptModel> queryAll();

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    DeptDto findById(Long id);

    /**
     * 根据PID查询
     *
     * @param pid /
     * @return /
     */
    List<DeptModel> findByPid(long pid);

    /**
     * 根据角色ID查询
     *
     * @param id /
     * @return /
     */
    Set<DeptModel> findByRoleId(Long roleId);

    /**
     * 构建树形数据
     *
     * @param deptDtos /
     * @return /
     */
    Object buildTree(List<DeptDto> deptDtos);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(DeptModel resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(DeptModel resources);

    /**
     * 删除
     *
     * @param deptDtos /
     */
    void delete(Set<DeptDto> deptDtos);

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<DeptDto> deptDtos, HttpServletResponse response) throws IOException;

    /**
     * 获取待删除的部门
     *
     * @param deptList /
     * @param deptDtos /
     * @return /
     */
    Set<DeptDto> getDeleteDepts(List<DeptModel> deptList, Set<DeptDto> deptDtos);

    /**
     * 根据ID获取同级与上级数据
     *
     * @param deptDto /
     * @param depts   /
     * @return /
     */
    List<DeptDto> getSuperior(DeptDto deptDto, List<DeptModel> depts);

    /**
     * 获取
     *
     * @param deptId
     * @param deptList
     * @return
     */
    List<Long> getDeptChildren(Long deptId, List<DeptModel> deptList);

    /**
     * 验证是否被角色或用户关联
     *
     * @param deptDtos /
     */
    void verification(Set<DeptDto> deptDtos);
}
