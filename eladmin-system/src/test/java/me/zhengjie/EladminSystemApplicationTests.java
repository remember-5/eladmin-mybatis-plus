package me.zhengjie;

import com.admin.AppRun;
import com.admin.modules.system.service.dto.DeptQueryCriteria;
import com.admin.system.mapper.IDeptMapper;
import com.admin.system.mapper.IDictDetailMapper;
import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = AppRun.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EladminSystemApplicationTests {

    @Autowired
    private IDeptMapper deptMapper;

    @Autowired
    private IDictDetailMapper dictDetailMapper;

    @Autowired
    private Mapper mapper;

    @Test
    public void contextLoads() throws Exception {
        System.out.println("***********************************************");

        DeptQueryCriteria criteria = new DeptQueryCriteria();
        criteria.setEnabled(true);
        criteria.setName("ff");
        criteria.setPid(null);
        List<Timestamp> createTimes = new ArrayList<>();
        createTimes.add(new Timestamp(new Date(2019 - 1900, 1, 1).getTime()));
        createTimes.add(new Timestamp(new Date(2020 - 1900, 1, 1).getTime() + 100000));
        criteria.setCreateTime(createTimes);
//        List<DeptDto> list = deptService.queryAll(criteria, true);

//        DeployQueryCriteria criteria = new DeployQueryCriteria();
//        criteria.setAppName("eladmin");
//        List<Timestamp> createTimes = new ArrayList<>();
//        createTimes.add(new Timestamp(new Date(2019 - 1900, 1, 1).getTime()));
//        createTimes.add(new Timestamp(new Date(2020 - 1900, 1, 1).getTime() + 100000));
//        criteria.setCreateTime(createTimes);
//        List<DeployDto> list = deployService.queryAll(criteria);
//        System.out.println(list);
//        verificationCodeRepository.findByScenesAndTypeAndValueAndStatusIsTrue("1", "2", "3");
//        qiniuContentRepository.findByKey("1");
//        Set<Long> roleIds = new HashSet<>();
//        roleIds.add(1L);
//        menuRepository.findByRoles_IdInAndTypeNotOrderBySortAsc(roleIds, 2);

//        Set<Dept> depts = deptRepository.findByRoles_Id(2L);
//        System.out.println("***********************************************");
//        System.out.println(depts);
//        System.out.println("***********************************************");
//        Set<DeptModel> depts2 = deptMapper.selectByRoleId(2L);
//        System.out.println("***********************************************");
//        System.out.println(depts2);

//        DeptQueryCriteria criteria = new DeptQueryCriteria();
//        criteria.setEnabled(true);
//        criteria.setName("ff");
//        List<Timestamp> createTimes = new ArrayList<>();
//        createTimes.add(new Timestamp(new Date().getTime()));
//        createTimes.add(new Timestamp(new Date().getTime() + 100000));
//        criteria.setCreateTime(createTimes);
//        QueryWrapper<DeptModel> query = new QueryWrapper<DeptModel>();
//        if (CollectionUtils.isNotEmpty(criteria.getIds())) {
//            query.lambda().in(DeptModel::getId, criteria.getIds());
//        }
//        if (StringUtils.isNotEmpty(criteria.getName())) {
//            query.lambda().like(DeptModel::getName, criteria.getName());
//        }
//        if (null != criteria.getEnabled()) {
//            query.lambda().eq(DeptModel::getEnabled, criteria.getEnabled());
//        }
//        if (null != criteria.getPid()) {
//            query.lambda().like(DeptModel::getPid, criteria.getPid());
//        }
//        if (CollectionUtils.isNotEmpty(criteria.getCreateTime()) && criteria.getCreateTime().size() >= 2) {
//            query.lambda().between(DeptModel::getCreateTime, criteria.getCreateTime().get(0),
//                    criteria.getCreateTime().get(1));
//        }
//        List<DeptModel> list = deptMapper.selectList(query);
//        List<DeptDto> dtoList = DozerUtils.mapList(mapper, list, DeptDto.class);
//        System.out.println(dtoList);
//        Pageable pageable = new PageRequest(0, 20);
//        Page<Role> test = roleService.findAll(pageable);
//        Object test = roleService.queryAll(pageable);
//        System.out.println(test);
//        DictDetailQueryCriteria dictCriteria = new DictDetailQueryCriteria();
//        dictCriteria.setDictName("user");
//        dictCriteria.setLabel("启用");
//        QueryWrapper<DictDetailModel> query = new QueryWrapper<DictDetailModel>();
//        IPage<DictDetailModel> pageable = new Page<DictDetailModel>(2, 10);
//        dictDetailMapper.selectJoin(pageable, dictCriteria);
        System.out.println("***********************************************");
    }

    public static void main(String[] args) {
    }
}
