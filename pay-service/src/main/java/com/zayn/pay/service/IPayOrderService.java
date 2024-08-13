package com.zayn.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zayn.pay.domain.dto.PayApplyDTO;
import com.zayn.pay.domain.dto.PayOrderFormDTO;
import com.zayn.pay.domain.po.PayOrder;

/**
 * <p>
 * 支付订单 服务类
 * </p>
 */
public interface IPayOrderService extends IService<PayOrder> {
    
    String applyPayOrder(PayApplyDTO applyDTO);
    
    void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO);
    
    void updatePayOrderByBizOrderNo(Long orderId, Integer i);
}
