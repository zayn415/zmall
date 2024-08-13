package com.zayn.cart;

import com.zayn.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 购物车服务
 */
@EnableFeignClients(basePackages = "com.zayn.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.zayn.cart.mapper")
@SpringBootApplication
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
