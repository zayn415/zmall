package com.zayn;

import com.zayn.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zayn
 * * @date 2024/7/7/下午10:50
 */
@EnableFeignClients(basePackages = "com.zayn.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.zayn.user.mapper")
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}