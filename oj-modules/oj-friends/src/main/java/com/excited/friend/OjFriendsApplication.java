package com.excited.friend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan("com.excited.**.mapper")
public class OjFriendsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjFriendsApplication.class, args);
    }
}
