package com.zayn.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zayn
 * * @date 2024/7/9/下午8:24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {
    private Integer maxItemCount;
}
