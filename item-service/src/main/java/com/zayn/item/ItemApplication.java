package com.zayn.item;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zayn
 * * @date 2024/7/7/下午11:20
 */
@MapperScan("com.zayn.item.mapper")
@SpringBootApplication
public class ItemApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ItemApplication.class, args);
    }
}