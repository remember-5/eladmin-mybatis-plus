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
package com.admin.log.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.admin.LogQueryCriteria;
import com.admin.dto.LogErrorDTO;
import com.admin.dto.LogSmallDTO;
import com.admin.log.mapper.ILogMapper;
import com.admin.log.model.LogModel;
import com.admin.log.service.ILogService;
import com.admin.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author adyfang
 * @date 2020年5月3日
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AdminLogServiceImpl extends ServiceImpl<ILogMapper, LogModel> implements ILogService {

    private final Mapper mapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object queryAll(LogQueryCriteria criteria, IPage pageable) {
        IPage<LogModel> page = this.page(pageable, buildWrapper(criteria));
        String status = "ERROR";
        if (status.equals(criteria.getLogType())) {
            List<LogErrorDTO> dtoList = DozerUtils.mapList(mapper, page.getRecords(), LogErrorDTO.class);
            return PageUtil.toPage(dtoList, page.getTotal());

        }
        return page;
    }

    @Override
    public List<LogModel> queryAll(LogQueryCriteria criteria) {
        return this.list(buildWrapper(criteria));
    }

    private QueryWrapper<LogModel> buildWrapper(LogQueryCriteria criteria) {
        QueryWrapper<LogModel> query = null;
        if (null != criteria) {
            boolean haveTime = CollectionUtils.isNotEmpty(criteria.getCreateTime())
                    && criteria.getCreateTime().size() >= 2;
            Timestamp start = haveTime ? criteria.getCreateTime().get(0) : null;
            Timestamp end = haveTime ? criteria.getCreateTime().get(1) : null;
            query = new QueryWrapper<LogModel>();
            query.lambda()
                    .nested(StringUtils.isNotEmpty(criteria.getBlurry()),
                            i -> i.like(LogModel::getUsername, criteria.getBlurry()).or()
                                    .like(LogModel::getDescription, criteria.getBlurry()).or()
                                    .like(LogModel::getAddress, criteria.getBlurry()).or()
                                    .like(LogModel::getRequestIp, criteria.getBlurry()).or()
                                    .like(LogModel::getMethod, criteria.getBlurry()).or()
                                    .like(LogModel::getParams, criteria.getBlurry()))
                    .eq(StringUtils.isNotEmpty(criteria.getLogType()), LogModel::getLogType, criteria.getLogType())
                    .between(haveTime, LogModel::getCreateTime, start, end);
        }
        return query;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, IPage pageable) {
        IPage<LogModel> page = this.page(pageable, buildWrapper(criteria));
        List<LogSmallDTO> dtoList = DozerUtils.mapList(mapper, page.getRecords(), LogSmallDTO.class);
        return PageUtil.toPage(dtoList, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, LogModel log) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.admin.annotation.Log aopLog = method.getAnnotation(com.admin.annotation.Log.class);

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";

        StringBuilder params = new StringBuilder("{");
        // 参数值
        //参数值
        List<Object> argValues = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        // 参数名称
        //参数名称
        for (Object argValue : argValues) {
            params.append(argValue).append(" ");
        }
        // 描述
        if (log != null) {
            log.setDescription(aopLog.value());
        }
        assert log != null;
        log.setRequestIp(ip);

        String loginPath = "login";
        if (loginPath.equals(signature.getName())) {
            try {
                assert argValues != null;
                username = new JSONObject(argValues.get(0)).get("username").toString();
            } catch (Exception e) {
                AdminLogServiceImpl.log.error(e.getMessage(), e);
            }
        }
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        log.setMethod(methodName);
        log.setUsername(username);
        log.setParams(params.toString() + " }");
        log.setBrowser(browser);
        this.saveOrUpdate(log);
    }

    @Override
    public Object findByErrDetail(Long id) {
        LogModel log = Optional.ofNullable(this.getById(id)).orElseGet(LogModel::new);
        ValidationUtil.isNull(log.getId(), "Log", "id", id);
        byte[] details = log.getExceptionDetail();
        return Dict.create().set("exception", new String(ObjectUtil.isNotNull(details) ? details : "".getBytes()));
    }

    @Override
    public void download(List<LogModel> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LogModel log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP来源", log.getAddress());
            map.put("描述", log.getDescription());
            map.put("浏览器", log.getBrowser());
            map.put("请求耗时/毫秒", log.getTime());
            map.put("异常详情", new String(
                    ObjectUtil.isNotNull(log.getExceptionDetail()) ? log.getExceptionDetail() : "".getBytes()));
            map.put("创建日期", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByError() {
        QueryWrapper<LogModel> query = new QueryWrapper<LogModel>();
        query.lambda().eq(LogModel::getLogType, "ERROR");
        this.remove(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByInfo() {
        QueryWrapper<LogModel> query = new QueryWrapper<LogModel>();
        query.lambda().eq(LogModel::getLogType, "INFO");
        this.remove(query);
    }
}
