package com.zayn.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zayn.trade.constants.MQConstants;
import com.zayn.trade.domain.dto.OrderFormDTO;
import com.zayn.trade.domain.po.Order;
import com.zayn.trade.domain.po.OrderDetail;
import com.zayn.trade.mapper.OrderMapper;
import com.zayn.trade.service.IOrderDetailService;
import com.zayn.trade.service.IOrderService;
import com.zayn.common.exception.BadRequestException;
import com.zayn.common.utils.UserContext;
import com.zayn.api.client.CartClient;
import com.zayn.api.client.ItemClient;
import com.zayn.api.dto.ItemDTO;
import com.zayn.api.dto.OrderDetailDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    
    private final ItemClient itemClient;
    private final IOrderDetailService detailService;
    private final CartClient cartClient;
    private final RabbitTemplate rabbitTemplate;
//    private final PayClient payClient;
    
    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 1.订单数据
        Order order = new Order();
        // 1.1.查询商品
        List<OrderDetailDTO> detailDTOS = orderFormDTO.getDetails();
        // 1.2.获取商品id和数量的Map
        Map<Long, Integer> itemNumMap = detailDTOS.stream()
                                                  .collect(Collectors.toMap(OrderDetailDTO::getItemId, OrderDetailDTO::getNum));
        Set<Long> itemIds = itemNumMap.keySet();
        // 1.3.查询商品
        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
        if (items == null || items.size() < itemIds.size()) {
            throw new BadRequestException("商品不存在");
        }
        // 1.4.基于商品价格、购买数量计算商品总价：totalFee
        int total = 0;
        for (ItemDTO item : items) {
            total += item.getPrice() * itemNumMap.get(item.getId());
        }
        order.setTotalFee(total);
        // 1.5.其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUser());
        order.setStatus(1);
        // 1.6.将Order写入数据库order表中
        save(order);
        
        // 2.保存订单详情
        List<OrderDetail> details = buildDetails(order.getId(), items, itemNumMap);
        detailService.saveBatch(details);
        
        // 3.清理购物车商品
        cartClient.deleteCartItemByIds(itemIds);
        
        // 4.扣减库存
        try {
            itemClient.deductStock(detailDTOS);
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }
        
        // 5. 延时消息
        rabbitTemplate.convertAndSend(
                MQConstants.DELAY_EXCHANGE_NAME,
                MQConstants.DELAY_ORDER_KEY,
                order.getId(),
                message -> {
                    message.getMessageProperties().setDelay(1000 * 60 * 30);
                    return message;
                });
        return order.getId();
    }
    
    @Override
    public void markOrderPaySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }
    
    /**
     * 取消订单2，恢复库存
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
//        // 标记为取消
//        lambdaUpdate()
//                .eq(Order::getId, orderId)
//                .set(Order::getStatus, 5)
//                .set(Order::getCloseTime, LocalDateTime.now())
//                .update();
//        //
//        payClient.updatePayOrderByBizOrderNo(orderId, 5);
//        // 恢复库存
//        List<OrderDetail> list = detailService.lambdaQuery()
//                                              .eq(OrderDetail::getOrderId, orderId)
//                                              .list();
//        List<OrderDetailDTO> orderDetailDTOS = BeanUtils.copyList(list, OrderDetailDTO.class);
//        itemClient.recoverStock(orderDetailDTOS);
    }
    
    private List<OrderDetail> buildDetails(Long orderId, List<ItemDTO> items, Map<Long, Integer> numMap) {
        List<OrderDetail> details = new ArrayList<>(items.size());
        for (ItemDTO item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setSpec(item.getSpec());
            detail.setPrice(item.getPrice());
            detail.setNum(numMap.get(item.getId()));
            detail.setItemId(item.getId());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            details.add(detail);
        }
        return details;
    }
}
