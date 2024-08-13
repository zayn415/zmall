package com.zayn.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zayn.cart.domain.dto.CartFormDTO;
import com.zayn.cart.domain.po.Cart;
import com.zayn.cart.domain.vo.CartVO;


import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 订单详情表 服务类
 * </p>
 *
 */
public interface ICartService extends IService<Cart> {

    void addItem2Cart(CartFormDTO cartFormDTO);

    List<CartVO> queryMyCarts();

    void removeByItemIds(Collection<Long> itemIds);
}
