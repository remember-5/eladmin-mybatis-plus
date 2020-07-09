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
package me.zhengjie;

import com.admin.AppRun;
import com.admin.modules.system.service.dto.DeptDto;
import com.admin.modules.system.service.dto.UserDto;
import com.admin.system.model.DeptModel;
import com.admin.system.model.UserModel;
import junit.framework.TestCase;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author adyfang
 * @date 2020年4月28日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest(classes = AppRun.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DozerTest extends TestCase {
    @Autowired
    private Mapper mapper;

    @Test
    public void testNotSameAttributeMapping() {
//        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-dozer.xml");
//        Mapper mapper = (Mapper) ctx.getBean("org.dozer.Mapper");
        DeptModel src = new DeptModel();
        src.setId(1L);
        src.setName("test");
        src.setCreateTime(new Timestamp(new Date().getTime()));
        DeptDto desc = mapper.map(src, DeptDto.class);
        Assert.assertNotNull(desc);
        UserModel user = new UserModel();
        user.setId(1L);
        UserDto userDto = mapper.map(user, UserDto.class);
        assertEquals(user.getId(), userDto.getId());
    }
}
