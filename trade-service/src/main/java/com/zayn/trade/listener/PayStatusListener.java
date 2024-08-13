package com.zayn.trade.listener;

import com.zayn.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author zayn
 * * @date 2024/7/12/下午12:06
 */
@Component
@RequiredArgsConstructor
public class PayStatusListener {
    
    private final IOrderService orderService;
    
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.pay.success.queue", durable = "true"),
            exchange = @Exchange("pay.direct"),
            key = "pay.success"
    ))
    public void ListenPayStatus(Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}
