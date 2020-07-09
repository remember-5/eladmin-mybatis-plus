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
package com.admin.tools.service;

import com.admin.dto.LocalStorageDto;
import com.admin.dto.LocalStorageQueryCriteria;
import com.admin.tools.model.LocalStorageModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
public interface ILocalStorageService extends IService<LocalStorageModel> {

    /**
     * @param id
     * @return
     */
    LocalStorageDto findById(Long id);

    /**
     * @param criteria
     * @return
     */
    List<LocalStorageDto> queryAll(LocalStorageQueryCriteria criteria);

    /**
     * @param name
     * @param multipartFile
     */
    void create(String name, MultipartFile multipartFile);

    /**
     * @param resources
     */
    void update(LocalStorageModel resources);

    /**
     * @param ids
     */
    void deleteAll(Long[] ids);

    /**
     * @param queryAll
     * @param response
     * @throws IOException
     */
    void download(List<LocalStorageDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * @param criteria
     * @param pageable
     * @return
     */
    @SuppressWarnings("rawtypes")
    Object queryAll(LocalStorageQueryCriteria criteria, IPage pageable);

}
