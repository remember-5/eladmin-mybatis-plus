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

import com.admin.modules.system.service.dto.JobDto;
import com.admin.modules.system.service.dto.JobQueryCriteria;
import com.admin.system.model.JobModel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月1日
 */
public interface IJobService extends IService<JobModel> {

    void download(List<JobDto> jobDtos, HttpServletResponse response) throws IOException;

    void delete(Set<Long> ids);

    void update(JobModel resources);

    void create(JobModel resources);

    JobDto findById(Long id);

    List<JobDto> queryAll(JobQueryCriteria criteria);

    @SuppressWarnings("rawtypes")
    Map<String, Object> queryAll(JobQueryCriteria criteria, IPage pageable);

    List<JobDto> queryAll();

    /**
     * 验证是否被用户关联
     *
     * @param ids /
     */
    void verification(Set<Long> ids);
}
