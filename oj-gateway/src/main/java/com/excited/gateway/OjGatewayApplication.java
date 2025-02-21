package com.excited.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 由于网关服务引入了 oj-common-core, 该模块下引入了 mybatis-plus 相关依赖
// 因此如果没有配置数据源就会导致 DataSourceAutoConfiguration 该类的 bean 对象注册失败
// 因此需要排除掉该类 DataSourceAutoConfiguration (数据源自动配置类)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OjGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjGatewayApplication.class, args);
    }
}
