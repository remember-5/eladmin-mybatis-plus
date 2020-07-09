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
package com.admin.system.quartz.service;

import com.admin.modules.quartz.service.dto.JobQueryCriteria;
import com.admin.system.quartz.model.QuartzJobModel;
import com.admin.system.quartz.model.QuartzLogModel;
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
public interface IQuartzJobService extends IService<QuartzJobModel> {

    /**
     * @param queryAllLog
     * @param response
     * @throws IOException
     */
    void downloadLog(List<QuartzLogModel> queryAllLog, HttpServletResponse response) throws IOException;

    /**
     * @param quartzJobs
     * @param response
     * @throws IOException
     */
    void download(List<QuartzJobModel> quartzJobs, HttpServletResponse response) throws IOException;

    /**
     * @param ids
     */
    void delete(Set<Long> ids);

    /**
     * @param quartzJob
     */
    void execution(QuartzJobModel quartzJob);

    /**
     * @param quartzJob
     */
    void updateIsPause(QuartzJobModel quartzJob);

    /**
     * @param resources
     */
    void update(QuartzJobModel resources);

    /**
     * @param resources
     */
    void create(QuartzJobModel resources);

    /**
     * @param id
     * @return
     */
    QuartzJobModel findById(Long id);

    /**
     * @param criteria
     * @return
     */
    List<QuartzLogModel> queryAllLog(JobQueryCriteria criteria);

    /**
     * @param criteria
     * @return
     */
    List<QuartzJobModel> queryAll(JobQueryCriteria criteria);

    /**
     * @param criteria
     * @param pageable
     * @return
     */
    @SuppressWarnings("rawtypes")
    Object queryAll(JobQueryCriteria criteria, IPage pageable);

    /**
     * @param criteria
     * @param pageable
     * @return
     */
    @SuppressWarnings("rawtypes")
    Object queryAllLog(JobQueryCriteria criteria, IPage pageable);

    /**
     * 执行子任务
     *
     * @param tasks /
     * @throws InterruptedException /
     */
    void executionSubJob(String[] tasks) throws InterruptedException;
}
