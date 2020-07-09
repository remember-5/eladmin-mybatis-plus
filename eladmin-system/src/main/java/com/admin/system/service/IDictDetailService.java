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

import com.admin.modules.system.service.dto.DictDetailDto;
import com.admin.modules.system.service.dto.DictDetailQueryCriteria;
import com.admin.system.model.DictDetailModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author adyfang
 * @date 2020年5月2日
 */
public interface IDictDetailService extends IService<DictDetailModel> {

    void delete(Long id);

    void update(DictDetailModel resources);

    void create(DictDetailModel resources);

    @SuppressWarnings("rawtypes")
    Map<String, Object> queryAll(DictDetailQueryCriteria criteria, IPage pageable);

    /**
     * 根据字典名称获取字典详情
     *
     * @param name 字典名称
     * @return /
     */
    List<DictDetailDto> getDictByName(String name);
}
