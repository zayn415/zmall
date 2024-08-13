package com.zayn.trade.listener;

import com.zayn.trade.constants.MQConstants;
import com.zayn.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author zayn
 * * @date 2024/7/15/下午3:21
 */
@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {
    private final IOrderService orderService;
    
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.DELAY_ORDER_QUEUE_NAME),
            exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE_NAME, delayed = "true"),
            key = MQConstants.DELAY_ORDER_KEY
    ))
    public void listenDelayMessage(Long orderId) {
//        System.out.println("订单超时未支付，关闭订单：" + orderId);
//        // 1. 查询订单
//        Order order = orderService.getById(orderId);
//
//        // 2. 检查订单状态
//        if (order == null || order.getStatus() != 1) {
//            // 订单不存在或已支付
//            return;
//        }
//
//        // 3. 未支付，查询流水
//        PayOrderDTO payOrderDTO = payClient.queryPayOrderByBizOrderNo(orderId);
//        if (payOrderDTO != null && payOrderDTO.getStatus() == 3) {
//            // 已支付
//            orderService.markOrderPaySuccess(orderId);
//        } else {
//            // 未支付，取消订单，恢复库存
//            orderService.cancelOrder(orderId);
//        }
    }
}
