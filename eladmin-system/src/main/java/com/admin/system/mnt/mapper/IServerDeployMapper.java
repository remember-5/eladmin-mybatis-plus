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

import com.admin.system.mnt.model.ServerDeployModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author adyfang
 * @date 2020年5月5日
 */
public interface IServerDeployMapper extends BaseMapper<ServerDeployModel> {
    @Select({
            "<script>SELECT s.*, d.deploy_id as deployId FROM mnt_server s LEFT OUTER JOIN mnt_deploy_server ds ON ds.server_id = s.server_id LEFT OUTER JOIN mnt_deploy d ON ds.deploy_id = d.deploy_id WHERE ds.deploy_id IN"
                    + "<foreach item='item' index='index' collection='deployIds' open='(' separator=',' close=')'> #{item} </foreach>"
                    + "</script>"})
    List<ServerDeployModel> selectByDeployIds(@Param("deployIds") Set<Long> deployIds);
}
