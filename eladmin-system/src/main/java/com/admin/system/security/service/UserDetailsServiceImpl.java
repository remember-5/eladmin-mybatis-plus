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
package com.admin.system.security.service;

import com.admin.config.LoginProperties;
import com.admin.exception.BadRequestException;
import com.admin.exception.EntityNotFoundException;
import com.admin.modules.system.service.dto.UserDto;
import com.admin.system.dto.AdminJwtUserDto;
import com.admin.system.service.IDataService;
import com.admin.system.service.IRoleService;
import com.admin.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
@Service("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserService userService;
    private final IRoleService roleService;
    private final IDataService dataService;
    private final LoginProperties loginProperties;
    /**
     * 用户信息缓存
     *
     * @see UserCacheClean
     */
    static Map<String, AdminJwtUserDto> userDtoCache = new ConcurrentHashMap<>();


    @Override
    public AdminJwtUserDto loadUserByUsername(String username) {
        boolean searchDb = true;
        AdminJwtUserDto adminJwtUserDto = null;
        if (loginProperties.isCacheEnable() && userDtoCache.containsKey(username)) {
            adminJwtUserDto = userDtoCache.get(username);
            searchDb = false;
        }
        if (searchDb) {
            UserDto user;
            try {
                user = userService.findByName(username);
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException("", e);
            }
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                if (!user.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                adminJwtUserDto = new AdminJwtUserDto(
                        user,
                        dataService.getDeptIds(user),
                        roleService.mapToGrantedAuthorities(user)
                );
                userDtoCache.put(username, adminJwtUserDto);
            }
        }
        return adminJwtUserDto;
    }
}
