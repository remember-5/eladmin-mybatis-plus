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
import com.admin.modules.system.service.dto.UserQueryCriteria;
import com.admin.system.model.UserModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author adyfang
 */
public interface IUserService extends IService<UserModel> {

    @SuppressWarnings("rawtypes")
    Object queryAll(UserQueryCriteria criteria, IPage pageable);

    List<UserDto> queryAll(UserQueryCriteria criteria);

    UserDto findById(long id);

    void create(UserModel resources);

    void update(UserModel resources);

    void updateCenter(UserModel resources);

    void delete(Set<Long> ids);

    UserDto findByName(String userName);

    void updatePass(String username, String pass);

    Map<String, String> updateAvatar(MultipartFile multipartFile);

    void updateEmail(String username, String email);

    void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException;

}
