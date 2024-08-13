package com.zayn.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zayn
 * * @date 2024/7/8/上午10:28
 */
@FeignClient("user-service")
public interface UserClient {
    /**
     * 扣减余额
     *
     * @param pw
     * @param amount
     */
    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
