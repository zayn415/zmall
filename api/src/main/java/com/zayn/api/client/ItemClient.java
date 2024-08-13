package com.zayn.api.client;


import com.zayn.api.config.DefaultFeignConfig;
import com.zayn.api.dto.ItemDTO;
import com.zayn.api.dto.OrderDetailDTO;
import com.zayn.api.fallback.ItemClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * @author zayn
 * * @date 2024/7/8/上午12:29
 */
@FeignClient(value = "item-service", configuration = DefaultFeignConfig.class, fallbackFactory = ItemClientFallback.class)
public interface ItemClient {
    /**
     * 根据id批量查询商品
     *
     * @param ids 商品id集合
     * @return 商品列表
     */
    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);
    
    /**
     * 扣减库存
     *
     * @param items 订单详情
     */
    @PutMapping("/items/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> items);
    
    @PutMapping("/items/stock/recover")
    void recoverStock(@RequestBody List<OrderDetailDTO> orderDetailDTOS);
}
