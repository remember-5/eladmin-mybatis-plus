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
package com.admin.system.quartz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.admin.exception.BadRequestException;
import com.admin.modules.quartz.service.dto.JobQueryCriteria;
import com.admin.system.quartz.mapper.IQuartzJobMapper;
import com.admin.system.quartz.mapper.IQuartzLogMapper;
import com.admin.system.quartz.model.QuartzJobModel;
import com.admin.system.quartz.model.QuartzLogModel;
import com.admin.system.quartz.service.IQuartzJobService;
import com.admin.system.quartz.utils.QuartzManage;
import com.admin.utils.FileUtil;
import com.admin.utils.PageUtil;
import com.admin.utils.RedisUtils;
import com.admin.utils.ValidationUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
@RequiredArgsConstructor
@Service(value = "quartzJobService")
public class AdminQuartzJobServiceImpl extends ServiceImpl<IQuartzJobMapper, QuartzJobModel>
        implements IQuartzJobService {

    private final IQuartzLogMapper quartzLogMapper;

    private final QuartzManage quartzManage;

    private final RedisUtils redisUtils;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAll(JobQueryCriteria criteria, IPage pageable) {
        IPage<QuartzJobModel> page = this.page(pageable, buildJobWrapper(criteria));
        return PageUtil.toPage(page.getRecords(), page.getTotal());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAllLog(JobQueryCriteria criteria, IPage pageable) {
        IPage<QuartzLogModel> page = quartzLogMapper.selectPage(pageable, buildLogWrapper(criteria));
        return PageUtil.toPage(page.getRecords(), page.getTotal());
    }

    @Override
    public List<QuartzJobModel> queryAll(JobQueryCriteria criteria) {
        return this.list(buildJobWrapper(criteria));
    }

    @Override
    public List<QuartzLogModel> queryAllLog(JobQueryCriteria criteria) {
        return quartzLogMapper.selectList(buildLogWrapper(criteria));
    }

    private QueryWrapper<QuartzJobModel> buildJobWrapper(JobQueryCriteria criteria) {
        QueryWrapper<QuartzJobModel> query = new QueryWrapper<QuartzJobModel>();
        if (null != criteria) {
            if (StringUtils.isNotEmpty(criteria.getJobName())) {
                query.lambda().like(QuartzJobModel::getJobName, criteria.getJobName());
            }
            if (CollectionUtils.isNotEmpty(criteria.getCreateTime()) && criteria.getCreateTime().size() >= 2) {
                query.lambda().between(QuartzJobModel::getCreateTime, criteria.getCreateTime().get(0),
                        criteria.getCreateTime().get(1));
            }
        }
        return query;
    }

    private QueryWrapper<QuartzLogModel> buildLogWrapper(JobQueryCriteria criteria) {
        QueryWrapper<QuartzLogModel> query = new QueryWrapper<QuartzLogModel>();
        if (null != criteria) {
            if (StringUtils.isNotEmpty(criteria.getJobName())) {
                query.lambda().like(QuartzLogModel::getJobName, criteria.getJobName());
            }
            if (null != criteria.getIsSuccess()) {
                query.lambda().eq(QuartzLogModel::getIsSuccess, criteria.getIsSuccess());
            }
            if (CollectionUtils.isNotEmpty(criteria.getCreateTime()) && criteria.getCreateTime().size() >= 2) {
                query.lambda().between(QuartzLogModel::getCreateTime, criteria.getCreateTime().get(0),
                        criteria.getCreateTime().get(1));
            }
        }
        return query;
    }

    @Override
    public QuartzJobModel findById(Long id) {
        QuartzJobModel quartzJob = Optional.ofNullable(this.getById(id)).orElseGet(QuartzJobModel::new);
        ValidationUtil.isNull(quartzJob.getId(), "QuartzJob", "id", id);
        return quartzJob;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(QuartzJobModel resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        this.save(resources);
        quartzManage.addJob(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QuartzJobModel resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())) {
            throw new BadRequestException("cron表达式格式错误");
        }
        if (StringUtils.isNotBlank(resources.getSubTask())) {
            List<String> tasks = Arrays.asList(resources.getSubTask().split("[,，]"));
            if (tasks.contains(resources.getId().toString())) {
                throw new BadRequestException("子任务中不能添加当前任务ID");
            }
        }
        this.saveOrUpdate(resources);
        quartzManage.updateJobCron(resources);
    }

    @Override
    public void updateIsPause(QuartzJobModel quartzJob) {
        if (quartzJob.getIsPause()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setIsPause(true);
        }
        this.saveOrUpdate(quartzJob);
    }

    @Override
    public void execution(QuartzJobModel quartzJob) {
        quartzManage.runJobNow(quartzJob);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        this.listByIds(ids).forEach(quartzJob -> quartzManage.deleteJob(quartzJob));
        this.removeByIds(ids);
    }

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executionSubJob(String[] tasks) throws InterruptedException {
        for (String id : tasks) {
            QuartzJobModel quartzJob = findById(Long.parseLong(id));
            // 执行任务
            String uuid = IdUtil.simpleUUID();
            quartzJob.setUuid(uuid);
            // 执行任务
            execution(quartzJob);
            // 获取执行状态，如果执行失败则停止后面的子任务执行
            Boolean result = (Boolean) redisUtils.get(uuid);
            while (result == null) {
                // 休眠5秒，再次获取子任务执行情况
                Thread.sleep(5000);
                result = (Boolean) redisUtils.get(uuid);
            }
            if (!result) {
                redisUtils.del(uuid);
                break;
            }
        }
    }

    @Override
    public void download(List<QuartzJobModel> quartzJobs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzJobModel quartzJob : quartzJobs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzJob.getJobName());
            map.put("Bean名称", quartzJob.getBeanName());
            map.put("执行方法", quartzJob.getMethodName());
            map.put("参数", quartzJob.getParams());
            map.put("表达式", quartzJob.getCronExpression());
            map.put("状态", quartzJob.getIsPause() ? "暂停中" : "运行中");
            map.put("描述", quartzJob.getDescription());
            map.put("创建日期", quartzJob.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadLog(List<QuartzLogModel> queryAllLog, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzLogModel quartzLog : queryAllLog) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("任务名称", quartzLog.getJobName());
            map.put("Bean名称", quartzLog.getBeanName());
            map.put("执行方法", quartzLog.getMethodName());
            map.put("参数", quartzLog.getParams());
            map.put("表达式", quartzLog.getCronExpression());
            map.put("异常详情", quartzLog.getExceptionDetail());
            map.put("耗时/毫秒", quartzLog.getTime());
            map.put("状态", quartzLog.getIsSuccess() ? "成功" : "失败");
            map.put("创建日期", quartzLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}
