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

import com.admin.exception.BadRequestException;
import com.admin.modules.system.service.dto.DeptDto;
import com.admin.modules.system.service.dto.DeptQueryCriteria;
import com.admin.system.mapper.IDeptMapper;
import com.admin.system.mapper.IRoleMapper;
import com.admin.system.mapper.IUserMapper;
import com.admin.system.model.DeptModel;
import com.admin.system.model.UserModel;
import com.admin.system.service.IDeptService;
import com.admin.utils.*;
import com.admin.utils.enums.DataScopeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author adyfang
 * @date 2020年4月27日
 */
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "dept")
public class AdminDeptServiceImpl extends ServiceImpl<IDeptMapper, DeptModel> implements IDeptService {

    private final Mapper mapper;

    private final RedisUtils redisUtils;

    private final IUserMapper userMapper;

    private final IRoleMapper roleMapper;

    @Override
    public List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception {
        String dataScopeType = SecurityUtils.getDataScopeType();
        if(dataScopeType.equals(DataScopeEnum.ALL.getValue())){
            criteria.setPidIsNull(true);
        }
        List<DeptDto> list = DozerUtils.mapList(mapper, this.list(buildWrapper(criteria, isQuery)), DeptDto.class);
        // 如果为空，就代表为自定义权限或者本级权限，就需要去重，不理解可以注释掉，看查询结果
        if(StringUtils.isBlank(dataScopeType)){
            return deduplication(list);
        }
        return list;
    }

    @Override
    @Cacheable
    public List<DeptModel> queryAll() {
        return this.list();
    }

    private QueryWrapper<DeptModel> buildWrapper(DeptQueryCriteria criteria, Boolean isQuery) {
        QueryWrapper<DeptModel> query = null;
        if (null != criteria) {
            query = new QueryWrapper<DeptModel>();
            if (isQuery) {
                criteria.setPidIsNull(true);
            }
            boolean notEmpty = StringUtils.isNotEmpty(criteria.getName()) || null != criteria.getPid()
                    || CollectionUtils.isNotEmpty(criteria.getCreateTime());
            if (isQuery && notEmpty) {
                criteria.setPidIsNull(null);
            }
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query.lambda().like(StringUtils.isNotEmpty(criteria.getName()), DeptModel::getName, criteria.getName())
                    .eq(null != criteria.getEnabled(), DeptModel::getEnabled, criteria.getEnabled())
                    .eq(null != criteria.getPid(), DeptModel::getPid, criteria.getPid())
                    .between(haveTime, DeptModel::getCreateTime, start, end)
                    .isNull(isQuery && null != criteria.getPidIsNull() && criteria.getPidIsNull(), DeptModel::getPid)
                    .orderByAsc(DeptModel::getDeptSort);
        }
        return query;
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    public DeptDto findById(Long id) {
        DeptModel dept = Optional.ofNullable(this.getById(id)).orElseGet(DeptModel::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", id);
        return mapper.map(dept, DeptDto.class);
    }

    @Cacheable(key = "'pid:' + #p0")
    public List<DeptModel> findByPid(long pid) {
        QueryWrapper<DeptModel> query = new QueryWrapper<DeptModel>();
        query.lambda().eq(DeptModel::getPid, pid);
        return this.list(query);
    }

    @Override
    public Set<DeptModel> findByRoleId(Long roleId) {
        return this.baseMapper.selectByRoleId(roleId);
    }

    @Override
    public List<DeptDto> getSuperior(DeptDto deptDto, List<DeptModel> depts) {
        if (deptDto.getPid() == null) {
            QueryWrapper<DeptModel> query = new QueryWrapper<DeptModel>();
            query.lambda().isNull(DeptModel::getPid);
            depts.addAll(this.list(query));
            return DozerUtils.mapList(mapper, depts, DeptDto.class);
        }
        QueryWrapper<DeptModel> query = new QueryWrapper<DeptModel>();
        query.lambda().eq(DeptModel::getPid, deptDto.getPid());
        depts.addAll(this.list(query));
        return getSuperior(findById(deptDto.getPid()), depts);
    }

    @Override
    public Object buildTree(List<DeptDto> deptDtos) {
        Set<DeptDto> trees = new LinkedHashSet<>();
        Set<DeptDto> depts = new LinkedHashSet<>();
        List<String> deptNames = deptDtos.stream().map(DeptDto::getName).collect(Collectors.toList());
        boolean isChild;
        for (DeptDto deptDTO : deptDtos) {
            isChild = false;
            if (deptDTO.getPid() == null) {
                trees.add(deptDTO);
            }
            for (DeptDto it : deptDtos) {
                if (it.getPid() != null && it.getPid().equals(deptDTO.getId())) {
                    isChild = true;
                    if (deptDTO.getChildren() == null) {
                        deptDTO.setChildren(new ArrayList<>());
                    }
                    deptDTO.getChildren().add(it);
                }
            }
            if (isChild) {
                depts.add(deptDTO);
            } else {
                DeptModel dept = null;
                if (null != deptDTO.getPid()) {
                    dept = this.getById(deptDTO.getPid());
                }
                if (null != dept && !deptNames.contains(dept.getName())) {
                    depts.add(deptDTO);
                }
            }
        }

        if (CollectionUtils.isEmpty(trees)) {
            trees = depts;
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("totalElements", deptDtos.size());
        map.put("content", CollectionUtils.isEmpty(trees) ? deptDtos : trees);
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeptModel resources) {
        this.save(resources);
        // 计算子节点数目
        resources.setSubCount(0);
        if (resources.getPid() != null) {
            // 清理缓存
            redisUtils.del("dept::pid:" + (resources.getPid() == null ? 0 : resources.getPid()));
            updateSubCnt(resources.getPid());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptModel resources) {
        // 旧的部门
        Long oldPid = findById(resources.getId()).getPid();
        Long newPid = resources.getPid();
        if (resources.getPid() != null && resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("上级不能为自己");
        }
        DeptModel dept = Optional.ofNullable(this.getById(resources.getId())).orElseGet(DeptModel::new);
        ValidationUtil.isNull(dept.getId(), "Dept", "id", resources.getId());
        resources.setId(dept.getId());
        this.updateById(resources);
        // 更新父节点中子节点数目
        updateSubCnt(oldPid);
        updateSubCnt(newPid);
        // 清理缓存
        delCaches(resources.getId(), oldPid, newPid);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<DeptDto> deptDtos) {
        this.removeByIds(deptDtos.stream().map(DeptDto::getId).collect(Collectors.toList()));
        deptDtos.forEach(deptDto -> {
            // 清理缓存
            delCaches(deptDto.getId(), deptDto.getPid(), null);
            baseMapper.deleteById(deptDto.getId());
            updateSubCnt(deptDto.getPid());
        });
    }

    @Override
    public void download(List<DeptDto> deptDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeptDto deptDTO : deptDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("部门名称", deptDTO.getName());
            map.put("部门状态", deptDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", deptDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Set<DeptDto> getDeleteDepts(List<DeptModel> deptList, Set<DeptDto> deptDtos) {
        for (DeptModel dept : deptList) {
            deptDtos.add(mapper.map(dept, DeptDto.class));
            List<DeptModel> depts = findByPid(dept.getId());
            if (depts != null && depts.size() != 0) {
                getDeleteDepts(depts, deptDtos);
            }
        }
        return deptDtos;
    }

    @Override
    public List<Long> getDeptChildren(Long deptId, List<DeptModel> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
            if (dept != null && dept.getEnabled()) {
                QueryWrapper<DeptModel> query = new QueryWrapper<DeptModel>();
                query.lambda().eq(DeptModel::getPid, dept.getId());
                List<DeptModel> depts = this.list(query);
                if (deptList.size() != 0) {
                    list.addAll(getDeptChildren(dept.getId(), depts));
                }
                list.add(dept.getId());
            }
        });
        return list;
    }

    private void updateSubCnt(Long deptId) {
        if (deptId != null) {
            QueryWrapper<DeptModel> query = new QueryWrapper<DeptModel>();
            query.lambda().eq(DeptModel::getPid, deptId);
            int count = this.count(query);

            UpdateWrapper<DeptModel> update = new UpdateWrapper<DeptModel>();
            update.lambda().eq(DeptModel::getId, deptId);
            DeptModel dept = new DeptModel();
            dept.setSubCount(count);
            this.update(dept, update);
        }
    }

    @Override
    public void verification(Set<DeptDto> deptDtos) {
        Set<Long> deptIds = deptDtos.stream().map(DeptDto::getId).collect(Collectors.toSet());
        if (userMapper.countByDepts(deptIds) > 0) {
            throw new BadRequestException("所选部门存在用户关联，请解除后再试！");
        }
        if (roleMapper.countByDepts(deptIds) > 0) {
            throw new BadRequestException("所选部门存在角色关联，请解除后再试！");
        }
    }

    /**
     * 清理缓存
     *
     * @param id     /
     * @param oldPid /
     * @param newPid /
     */
    public void delCaches(Long id, Long oldPid, Long newPid) {
        List<UserModel> users = userMapper.findByDeptRoleId(id);
        // 删除数据权限
        redisUtils.delByKeys("data::user:", users.stream().map(UserModel::getId).collect(Collectors.toSet()));
        redisUtils.del("dept::id:" + id);

        redisUtils.del("dept::pid:" + (oldPid == null ? 0 : oldPid));
        redisUtils.del("dept::pid:" + (newPid == null ? 0 : newPid));
    }


    private List<DeptDto> deduplication(List<DeptDto> list) {
        List<DeptDto> deptDtos = new ArrayList<>();
        for (DeptDto deptDto : list) {
            boolean flag = true;
            for (DeptDto dto : list) {
                if (dto.getId().equals(deptDto.getPid())) {
                    flag = false;
                    break;
                }
            }
            if (flag){
                deptDtos.add(deptDto);
            }
        }
        return deptDtos;
    }

}
