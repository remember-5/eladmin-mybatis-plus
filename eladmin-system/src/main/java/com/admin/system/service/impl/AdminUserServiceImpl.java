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

import com.admin.config.FileProperties;
import com.admin.exception.EntityExistException;
import com.admin.exception.EntityNotFoundException;
import com.admin.modules.system.service.dto.*;
import com.admin.system.mapper.IUserMapper;
import com.admin.system.mapper.IUsersJobsMapper;
import com.admin.system.model.*;
import com.admin.system.security.service.UserCacheClean;
import com.admin.system.service.IDeptService;
import com.admin.system.service.IJobService;
import com.admin.system.service.IUserService;
import com.admin.system.service.IUsersRolesService;
import com.admin.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author adyfang
 */
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "user")
public class AdminUserServiceImpl extends ServiceImpl<IUserMapper, UserModel> implements IUserService {
    private final Mapper mapper;
    private final RedisUtils redisUtils;
    private final FileProperties properties;
    private final IDeptService deptService;
    private final IJobService jobService;
    private final IUsersRolesService usersRolesService;
    private final IUsersJobsMapper usersJobsMapper;
    private final UserCacheClean userCacheClean;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(UserQueryCriteria criteria, IPage pageable) {
        IPage<UserModel> page = this.page(pageable, buildWrapper(criteria));
        if (page.getTotal() > 0) {
            Map<Long, DeptModel> deptMap = deptService.queryAll().parallelStream()
                    .collect(Collectors.toMap(DeptModel::getId, Function.identity(), (x, y) -> x));
            Map<Long, JobDto> jobMap = jobService.queryAll().parallelStream()
                    .collect(Collectors.toMap(JobDto::getId, Function.identity(), (x, y) -> x));
            QueryWrapper<UsersRolesModel> query = new QueryWrapper<UsersRolesModel>();
            query.lambda().in(UsersRolesModel::getUserId,
                    page.getRecords().stream().map(UserModel::getId).collect(Collectors.toSet()));
            Map<Long, Set<UsersRolesModel>> rolesMap = usersRolesService.list(query).stream()
                    .collect(Collectors.groupingBy(UsersRolesModel::getUserId, Collectors.toSet()));

            QueryWrapper<UsersJobsModel> queryJob = new QueryWrapper<UsersJobsModel>();
            queryJob.lambda().in(UsersJobsModel::getUserId,
                    page.getRecords().stream().map(UserModel::getId).collect(Collectors.toList()));
            Map<Long, List<UsersJobsModel>> userJobMap = usersJobsMapper.selectList(queryJob).stream()
                    .collect(Collectors.groupingBy(UsersJobsModel::getUserId));
            page.getRecords().forEach(user -> {
                if (rolesMap.containsKey(user.getId())) {
                    user.setRoles(rolesMap.get(user.getId()).stream().map(ur -> {
                        RoleModel role = new RoleModel();
                        role.setId(ur.getRoleId());
                        return role;
                    }).collect(Collectors.toSet()));
                }
                user.setDept(mapper.map(deptMap.get(user.getDeptId()), DeptModel.class));
                if (userJobMap.containsKey(user.getId())) {
                    Set<JobDto> jobs = userJobMap.get(user.getId()).stream().map(x -> {
                        return jobMap.get(x.getJobId());
                    }).filter(x -> null != x).collect(Collectors.toSet());
                    user.setJobs(DozerUtils.mapSet(mapper, jobs, JobModel.class));
                }
            });
        }
        List<UserDto> dtoList = DozerUtils.mapList(mapper, page.getRecords(), UserDto.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    private QueryWrapper<UserModel> buildWrapper(UserQueryCriteria criteria) {
        QueryWrapper<UserModel> query = null;
        if (null != criteria) {
            query = new QueryWrapper<UserModel>();
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query.lambda()
                    .nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                            i -> i.like(UserModel::getUsername, criteria.getBlurry()).or()
                                    .like(UserModel::getEmail, criteria.getBlurry()).or()
                                    .like(UserModel::getNickName, criteria.getBlurry()))
                    .eq(null != criteria.getId(), UserModel::getId, criteria.getId())
                    .eq(null != criteria.getEnabled(), UserModel::getEnabled, criteria.getEnabled())
                    .in(CollectionUtils.isNotEmpty(criteria.getDeptIds()), UserModel::getDeptId, criteria.getDeptIds())
                    .between(haveTime, UserModel::getCreateTime, start, end);
        }
        return query;
    }

    @Override
    public List<UserDto> queryAll(UserQueryCriteria criteria) {
        List<UserModel> users = this.list(buildWrapper(criteria));
        return DozerUtils.mapList(mapper, users, UserDto.class);
    }

    @Override
    @Cacheable(key = "'id:' + #p0")
    @Transactional(rollbackFor = Exception.class)
    public UserDto findById(long id) {
        UserModel user = Optional.ofNullable(this.getById(id)).orElseGet(UserModel::new);
        ValidationUtil.isNull(user.getId(), "User", "id", id);
        return mapper.map(user, UserDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(UserModel resources) {
        QueryWrapper<UserModel> query = new QueryWrapper<UserModel>();
        query.lambda().eq(UserModel::getUsername, resources.getUsername());
        if (this.getOne(query) != null) {
            throw new EntityExistException(UserModel.class, "username", resources.getUsername());
        }
        QueryWrapper<UserModel> query2 = new QueryWrapper<UserModel>();
        query2.lambda().eq(UserModel::getEmail, resources.getEmail());
        if (this.getOne(query2) != null) {
            throw new EntityExistException(UserModel.class, "email", resources.getEmail());
        }
        // 前端传参为dept对象id
        if (null != resources.getDept()) {
            resources.setDeptId(resources.getDept().getId());
        }
        this.save(resources);
        if (CollectionUtils.isNotEmpty(resources.getRoles())) {
            resources.getRoles().forEach(role -> {
                UsersRolesModel ur = new UsersRolesModel();
                ur.setUserId(resources.getId());
                ur.setRoleId(role.getId());
                usersRolesService.save(ur);
            });
        }
        if (CollectionUtils.isNotEmpty(resources.getJobs())) {
            resources.getJobs().forEach(job -> {
                UsersJobsModel uj = new UsersJobsModel();
                uj.setUserId(resources.getId());
                uj.setJobId(job.getId());
                usersJobsMapper.insert(uj);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserModel resources) {
        UserModel user = Optional.ofNullable(this.getById(resources.getId())).orElseGet(UserModel::new);
        ValidationUtil.isNull(user.getId(), "User", "id", resources.getId());
        QueryWrapper<UserModel> query = new QueryWrapper<UserModel>();
        query.lambda().eq(UserModel::getUsername, resources.getUsername());
        QueryWrapper<UserModel> query2 = new QueryWrapper<UserModel>();
        query2.lambda().eq(UserModel::getEmail, resources.getEmail());
        UserModel user1 = this.getOne(query);
        UserModel user2 = this.getOne(query2);

        if (user1 != null && !user.getId().equals(user1.getId())) {
            throw new EntityExistException(UserModel.class, "username", resources.getUsername());
        }

        if (user2 != null && !user.getId().equals(user2.getId())) {
            throw new EntityExistException(UserModel.class, "email", resources.getEmail());
        }

        // 如果用户的角色改变
        if (!resources.getRoles().equals(user.getRoles())) {
            redisUtils.del(CacheKey.DATE_USER + resources.getId());
            redisUtils.del(CacheKey.MENU_USER + resources.getId());
            redisUtils.del(CacheKey.ROLE_AUTH + resources.getId());
        }

        // 如果用户名称修改
        if (!resources.getUsername().equals(user.getUsername())) {
            redisUtils.del("user::username:" + user.getUsername());
        }

        if (CollectionUtils.isNotEmpty(resources.getRoles())) {
            QueryWrapper<UsersRolesModel> queryRoles = new QueryWrapper<UsersRolesModel>();
            queryRoles.lambda().eq(UsersRolesModel::getUserId, resources.getId());
            usersRolesService.remove(queryRoles);
            usersRolesService.saveBatch(resources.getRoles().stream().map(role -> {
                UsersRolesModel ur = new UsersRolesModel();
                ur.setRoleId(role.getId());
                ur.setUserId(resources.getId());
                return ur;
            }).collect(Collectors.toCollection(ArrayList::new)));
        }
        user.setUsername(resources.getUsername());
        user.setEmail(resources.getEmail());
        user.setEnabled(resources.getEnabled());
        user.setRoles(resources.getRoles());
        user.setDept(resources.getDept());
        user.setDeptId(resources.getDept().getId());
        user.setJobs(resources.getJobs());
        user.setPhone(resources.getPhone());
        user.setNickName(resources.getNickName());
        user.setGender(resources.getGender());
        this.saveOrUpdate(user);
        // 清除缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCenter(UserModel resources) {
        UserModel user = Optional.ofNullable(this.getById(resources.getId())).orElseGet(UserModel::new);
        user.setNickName(resources.getNickName());
        user.setPhone(resources.getPhone());
        user.setGender(resources.getGender());
        this.saveOrUpdate(user);
        // 清理缓存
        delCaches(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        QueryWrapper<UsersRolesModel> query = new QueryWrapper<UsersRolesModel>();
        query.lambda().in(UsersRolesModel::getUserId, ids);
        usersRolesService.remove(query);
        this.removeByIds(ids);

        for (Long id : ids) {
            // 清理缓存
            UserDto user = findById(id);
            delCaches(user.getId(), user.getUsername());
        }
    }

    @Override
    @Cacheable(key = "'username:' + #p0")
    public UserDto findByName(String userName) {
        QueryWrapper<UserModel> query = new QueryWrapper<UserModel>();
        query.lambda().eq(UserModel::getUsername, userName);
        UserModel user = this.baseMapper.selectLink(query);
        if (user == null) {
            throw new EntityNotFoundException(UserModel.class, "name", userName);
        } else {
            return mapper.map(user, UserDto.class);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePass(String username, String pass) {
        UpdateWrapper<UserModel> update = new UpdateWrapper<UserModel>();
        UserModel user = new UserModel();
        user.setPassword(pass);
        user.setPwdResetTime(new Date());
        update.lambda().eq(UserModel::getUsername, username);
        this.update(user, update);
        redisUtils.del("user::username:" + username);
        flushCache(username);
    }

    @SuppressWarnings("serial")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> updateAvatar(MultipartFile multipartFile) {
        QueryWrapper<UserModel> query = new QueryWrapper<UserModel>();
        query.lambda().eq(UserModel::getUsername, SecurityUtils.getCurrentUsername());
        UserModel user = this.baseMapper.selectLink(query);

        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        user.setAvatarName(file.getName());
        this.saveOrUpdate(user);
        if (StringUtils.isNotBlank(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = user.getUsername();
        redisUtils.del(CacheKey.USER_NAME + username);
        flushCache(username);
        return new HashMap<String, String>(1) {{
            put("avatar", file.getName());
        }};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String username, String email) {
        UpdateWrapper<UserModel> update = new UpdateWrapper<UserModel>();
        UserModel user = new UserModel();
        user.setEmail(email);
        update.lambda().eq(UserModel::getUsername, username);
        this.update(user, update);
        redisUtils.del("user::username:" + username);
    }

    @Override
    public void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDto userDTO : queryAll) {
            List<String> roles = userDTO.getRoles().stream().map(RoleSmallDto::getName).collect(Collectors.toList());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", userDTO.getUsername());
            map.put("角色", roles);
            map.put("部门", userDTO.getDept().getName());
            map.put("岗位", userDTO.getJobs().stream().map(JobSmallDto::getName).collect(Collectors.toList()));
            map.put("邮箱", userDTO.getEmail());
            map.put("状态", userDTO.getEnabled() ? "启用" : "禁用");
            map.put("手机号码", userDTO.getPhone());
            map.put("修改密码的时间", userDTO.getPwdResetTime());
            map.put("创建日期", userDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 清理缓存
     *
     * @param id /
     */
    public void delCaches(Long id, String username) {
        redisUtils.del(CacheKey.USER_ID + id);
        redisUtils.del(CacheKey.USER_NAME + username);
        flushCache(username);
    }

    /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username
     */
    private void flushCache(String username) {
        userCacheClean.cleanUserCache(username);
    }
}
