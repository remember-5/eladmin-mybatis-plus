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

import com.admin.modules.system.domain.vo.MenuVo;
import com.admin.modules.system.service.dto.MenuDto;
import com.admin.modules.system.service.dto.MenuQueryCriteria;
import com.admin.system.model.MenuModel;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
public interface IMenuService extends IService<MenuModel> {

    void download(List<MenuDto> menuDtos, HttpServletResponse response) throws IOException;

    MenuDto findById(long id);

    void create(MenuModel resources);

    void update(MenuModel resources);

    Set<MenuModel> getDeleteMenus(List<MenuModel> menuList, Set<MenuModel> menuSet);

    void delete(Set<MenuModel> menuSet);

    Object getMenuTree(Long pid);

    List<MenuDto> buildTree(List<MenuDto> menuDtos);

    List<MenuVo> buildMenus(List<MenuDto> menuDtos);

    MenuModel findOne(Long id);

    List<MenuDto> queryAll(MenuQueryCriteria criteria, Boolean isQuery);

    List<MenuDto> getMenus(Long pid);

    List<MenuDto> getSuperior(MenuDto menuDto, List<MenuModel> menus);

    /**
     * @param currentUserId
     * @return
     */
    List<MenuDto> findByUser(Long currentUserId);

}
