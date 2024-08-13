package com.zayn.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author zayn
 * * @date 2024/7/8/上午12:19
 */
@Configuration
public class RemoteCallConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
