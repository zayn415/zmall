package com.zayn.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * @author zayn
 * * @date 2024/7/8/上午10:16
 */
@FeignClient("cart-service")
public interface CartClient {
    /**
     * 根据id查询商品
     *
     * @param ids 商品id集合
     */
    @DeleteMapping("/carts")
    void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);
}
