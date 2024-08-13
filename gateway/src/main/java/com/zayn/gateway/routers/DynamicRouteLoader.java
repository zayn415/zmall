package com.zayn.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author zayn
 * * @date 2024/7/9/下午8:44
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {
    
    public static final String group = "DEFAULT_GROUP"; // nacos配置文件的group
    public static final long timeoutMs = 5000; // nacos配置文件的超时时间
    private static final String dataId = "gateway-router.json"; // nacos配置文件的dataId
    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;
    private final Set<String> routeIds = new HashSet<>();
    
    @PostConstruct//项目启动时加载路由
    public void initRouteConfigListener() throws NacosException {
        // 项目启动时，加载路由配置，添加监听器
        String configInfo = nacosConfigManager.getConfigService()
                                              .getConfigAndSignListener(dataId, group, timeoutMs, new Listener() {
                                                  @Override
                                                  public Executor getExecutor() {
                                                      return null;
                                                  }
                                                  
                                                  @Override
                                                  public void receiveConfigInfo(String s) {
                                                      // 配置变更时重新加载路由
                                                      updateConfigInfo(s);
                                                  }
                                              });
        // 第一次读取配置，初始化路由
        log.info("init route config:{}", configInfo);
        updateConfigInfo(configInfo);
    }
    
    private void updateConfigInfo(String configInfo) {
        // todo 更新路由
        log.info("更新路由配置:{}", configInfo);
        // 解析配置信息
        List<RouteDefinition> routeDefinitionList = JSONUtil.toList(configInfo, RouteDefinition.class);
        
        // 删除原有路由，所有
        routeIds.forEach(id -> {
            log.info("删除路由:{}", id); // 输出日志
            routeDefinitionWriter.delete(Mono.just(id)).subscribe(); // 删除路由
        });
        
        // 更新路由
        for (RouteDefinition routeDefinition : routeDefinitionList) {
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe(); // 保存路由
            routeIds.add(routeDefinition.getId()); // 保存路由id
            log.info("保存路由:{}", routeDefinition.getId()); // 输出日志
        }
    }
}
