package com.zayn.pay.controller;

import com.zayn.pay.domain.dto.PayApplyDTO;
import com.zayn.pay.domain.dto.PayOrderFormDTO;
import com.zayn.pay.domain.po.PayOrder;
import com.zayn.pay.domain.vo.PayOrderVO;
import com.zayn.pay.enums.PayType;
import com.zayn.pay.service.IPayOrderService;
import com.zayn.common.exception.BizIllegalException;
import com.zayn.common.utils.BeanUtils;
import com.zayn.api.dto.PayOrderDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "支付相关接口")
@RestController
@RequestMapping("pay-orders")
@RequiredArgsConstructor
public class PayController {
    
    private final IPayOrderService payOrderService;
    
    @ApiOperation("更新支付单状态")
    @PutMapping("/update/{orderId}/{i}")
    public void updatePayOrderByBizOrderNo(@PathVariable Long orderId,
                                           @PathVariable Integer i) {
        payOrderService.updatePayOrderByBizOrderNo(orderId, i);
    }
    
    @ApiOperation("查询支付单")
    @GetMapping
    public List<PayOrderVO> queryPayOrders() {
        return BeanUtils.copyList(payOrderService.list(), PayOrderVO.class);
    }
    
    @ApiOperation("生成支付单")
    @PostMapping
    public String applyPayOrder(@RequestBody PayApplyDTO applyDTO) {
        if (!PayType.BALANCE.equalsValue(applyDTO.getPayType())) {
            // 目前只支持余额支付
            throw new BizIllegalException("抱歉，目前只支持余额支付");
        }
        return payOrderService.applyPayOrder(applyDTO);
    }
    
    @ApiOperation("尝试基于用户余额支付")
    @ApiImplicitParam(value = "支付单id", name = "id")
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO) {
        payOrderFormDTO.setId(id);
        payOrderService.tryPayOrderByBalance(payOrderFormDTO);
    }
    
    @ApiOperation("根据id查询支付单")
    @GetMapping("/biz/{id}")
    public PayOrderDTO queryPayOrderByBizOrderNo(@PathVariable("id") Long id) {
        PayOrder payOrder = payOrderService.lambdaQuery().eq(PayOrder::getBizOrderNo, id).one();
        return BeanUtils.copyBean(payOrder, PayOrderDTO.class);
    }
}
