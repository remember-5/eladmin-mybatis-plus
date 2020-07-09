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
package com.admin.system.mnt.mapper;

import com.admin.modules.mnt.service.dto.DeployQueryCriteria;
import com.admin.system.mnt.model.DeployModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
public interface IDeployMapper extends BaseMapper<DeployModel> {
    @Select({"<script>SELECT d.* FROM mnt_deploy d LEFT OUTER JOIN mnt_app a ON d.app_id=a.id "
            + "WHERE ( a.name LIKE CONCAT('%', #{criteria.appName}, '%')) "
            + "AND ( d.create_time BETWEEN #{startTime} AND #{endTime})" + "</script>"})
    Set<DeployModel> selectByRoleIds(@Param("criteria") DeployQueryCriteria criteria, Timestamp startTime,
                                     Timestamp endTime);
}
