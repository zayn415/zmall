package com.zayn.api.fallback;

/**
 * @author zayn
 * * @date 2024/7/10/下午6:02
 */

import com.zayn.common.utils.CollUtils;
import com.zayn.api.client.ItemClient;
import com.zayn.api.dto.ItemDTO;
import com.zayn.api.dto.OrderDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

/**
 * 用于生成ItemClient的fallback工厂类
 */
@Slf4j
public class ItemClientFallback implements FallbackFactory<ItemClient> {
    
    // 降级处理
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("查询商品信息失败", cause);
                return CollUtils.emptyList();
            }
            
            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("扣减库存失败", cause);
            }
            
            @Override
            public void recoverStock(List<OrderDetailDTO> orderDetailDTOS) {
                log.error("恢复库存失败", cause);
            }
        };
    }
}
