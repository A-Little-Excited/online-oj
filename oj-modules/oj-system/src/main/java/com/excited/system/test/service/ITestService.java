package com.excited.system.test.service;

import com.excited.system.test.domain.TestDomain;

import java.util.List;

public interface ITestService {
    List<TestDomain> list();

    String add();
}
