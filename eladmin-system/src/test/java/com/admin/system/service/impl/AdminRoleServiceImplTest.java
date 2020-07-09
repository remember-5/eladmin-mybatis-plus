package com.admin.system.service.impl;

import com.admin.AppRun;
import com.admin.system.model.RoleModel;
import com.admin.system.service.IRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = AppRun.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AdminRoleServiceImplTest {

    @Autowired
    IRoleService roleService;

    @Test
    public void findInMenuId() {
//		System.err.println("11");
        List<RoleModel> inMenuId = roleService.findInMenuId(Arrays.asList(1L, 2L));
        System.err.println(inMenuId);


    }
}