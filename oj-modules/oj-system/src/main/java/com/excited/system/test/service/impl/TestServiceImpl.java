package com.excited.system.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.system.test.domain.TestDomain;
import com.excited.system.test.mapper.TestMapper;
import com.excited.system.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements ITestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<TestDomain> list() {
        return testMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public String add() {
        TestDomain testDomain = new TestDomain();
        testDomain.setTitle("测试");
        testDomain.setContent("测试雪花算法");
        testMapper.insert(testDomain);

        return "添加成功";
    }
}
