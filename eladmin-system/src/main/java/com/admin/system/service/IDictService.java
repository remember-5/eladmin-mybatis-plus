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

import com.admin.modules.system.service.dto.DictDto;
import com.admin.modules.system.service.dto.DictQueryCriteria;
import com.admin.system.model.DictModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
public interface IDictService extends IService<DictModel> {

    DictDto findById(Long id);

    List<DictDto> queryAll(DictQueryCriteria dict);

    @SuppressWarnings("rawtypes")
    Map<String, Object> queryAll(DictQueryCriteria dict, IPage pageable);

    void create(DictModel resources);

    void update(DictModel resources);

    void delete(Set<Long> ids);

    void download(List<DictDto> dictDtos, HttpServletResponse response) throws IOException;

}
