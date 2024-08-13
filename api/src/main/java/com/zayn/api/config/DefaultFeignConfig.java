package com.zayn.api.config;

import com.zayn.common.utils.UserContext;
import com.zayn.api.fallback.ItemClientFallback;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;


/**
 * @author zayn
 * * @date 2024/7/8/上午1:06
 */
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    requestTemplate.header("user-info", userId.toString());
                }
                
            }
        };
    }
    
    @Bean
    public ItemClientFallback itemClientFallback() {
        return new ItemClientFallback();
    }
}
