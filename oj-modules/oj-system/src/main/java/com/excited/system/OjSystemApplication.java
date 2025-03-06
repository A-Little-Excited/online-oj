package com.excited.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 使用该注解后 mapper 类无需再使用 @Mapper, SpringBoot 启动后会自动扫描 @MapperScan 指定的路径下的 mapper 文件
@MapperScan("com.excited.**.mapper")
public class OjSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjSystemApplication.class, args);
    }
}
