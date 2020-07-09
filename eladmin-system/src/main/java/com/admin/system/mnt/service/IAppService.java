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
package com.admin.system.mnt.service;

import com.admin.modules.mnt.service.dto.AppDto;
import com.admin.modules.mnt.service.dto.AppQueryCriteria;
import com.admin.system.mnt.model.AppModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
public interface IAppService extends IService<AppModel> {

    /**
     * @param criteria
     * @param pageable
     * @return
     */
    @SuppressWarnings("rawtypes")
    Object queryAll(AppQueryCriteria criteria, IPage pageable);

    /**
     * @param criteria
     * @return
     */
    List<AppDto> queryAll(AppQueryCriteria criteria);

    /**
     * @param id
     * @return
     */
    AppDto findById(Long id);

    /**
     * @param resources
     */
    void create(AppModel resources);

    /**
     * @param resources
     */
    void update(AppModel resources);

    /**
     * @param queryAll
     * @param response
     * @throws IOException
     */
    void download(List<AppDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * @param ids
     */
    void delete(Set<Long> ids);

}
