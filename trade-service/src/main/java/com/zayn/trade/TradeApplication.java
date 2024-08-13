package com.zayn.trade;

import com.zayn.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zayn
 * * @date 2024/7/7/下午10:52
 */
@EnableFeignClients(basePackages = "com.zayn.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.zayn.trade.mapper")
@SpringBootApplication
public class TradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }
}