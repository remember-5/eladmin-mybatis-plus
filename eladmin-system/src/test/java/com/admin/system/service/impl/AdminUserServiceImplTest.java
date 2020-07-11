package com.admin.system.service.impl;

import com.admin.system.model.RoleModel;
import me.zhengjie.EladminSystemApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminUserServiceImplTest extends EladminSystemApplicationTests {

	@Autowired
	AdminRoleServiceImpl adminRoleService;

	@Test
	public void findById() {
		RoleModel byId = adminRoleService.getById(3);
		RoleModel roleModel = adminRoleService.getBaseMapper().selectById(3);
		System.err.println(byId);
		System.err.println(roleModel);
	}
}