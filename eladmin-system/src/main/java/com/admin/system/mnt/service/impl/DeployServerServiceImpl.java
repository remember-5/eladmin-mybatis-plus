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
package com.admin.system.mnt.service.impl;

import com.admin.system.mnt.mapper.IDeployServerMapper;
import com.admin.system.mnt.model.DeployServerModel;
import com.admin.system.mnt.service.IDeployServerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author adyfang
 * @date 2020年5月13日
 */
@RequiredArgsConstructor
@Service
public class DeployServerServiceImpl extends ServiceImpl<IDeployServerMapper, DeployServerModel>
        implements IDeployServerService {
}
