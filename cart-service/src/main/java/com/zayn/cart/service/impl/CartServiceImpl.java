package com.zayn.cart.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zayn.cart.config.CartProperties;
import com.zayn.cart.domain.dto.CartFormDTO;
import com.zayn.cart.domain.po.Cart;
import com.zayn.cart.domain.vo.CartVO;
import com.zayn.cart.mapper.CartMapper;
import com.zayn.cart.service.ICartService;
import com.zayn.common.exception.BizIllegalException;
import com.zayn.common.utils.BeanUtils;
import com.zayn.common.utils.CollUtils;
import com.zayn.common.utils.UserContext;
import com.zayn.api.client.ItemClient;
import com.zayn.api.dto.ItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {
    
    private final ItemClient itemClient;
    
    private final CartProperties cartProperties;
    
    /**
     * 添加商品到购物车
     * 1. 判断是否已经存在，存在则更新数量
     * 2. 不存在则判断是否超过购物车数量限制
     * 3. 新增购物车条目
     * 3.1. 转换PO
     * 3.2. 保存当前用户：从ThreadLocal中获取
     * 3.3. 保存到数据库
     *
     * @param cartFormDTO
     */
    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        
        // 2.判断是否已经存在
        if (checkItemExists(cartFormDTO.getItemId(), userId)) {
            // 2.1.存在，则更新数量
            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            return;
        }
        // 2.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);
        
        // 3.新增购物车条目
        // 3.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 3.2.保存当前用户
        cart.setUserId(userId);
        // 3.3.保存到数据库
        save(cart);
    }
    
    @Override
    public List<CartVO> queryMyCarts() {
        // 1.查询我的购物车列表
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, UserContext.getUser()).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }
        
        // 2.转换VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);
        
        // 3.处理VO中的商品信息
        handleCartItems(vos);
        
        // 4.返回
        return vos;
    }
    
    /**
     * 处理购物车中的商品信息
     *
     * @param vos 购物车VO列表
     */
    private void handleCartItems(List<CartVO> vos) {
        // 1.获取商品id
        Set<Long> itemIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());
        // 2.查询商品
        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
        if (CollUtils.isEmpty(items)) {
            return;
        }
        // 3.转为 id 到 item的map
        Map<Long, ItemDTO> itemMap = items.stream().collect(Collectors.toMap(ItemDTO::getId, Function.identity()));
        // 4.写入vo
        for (CartVO v : vos) {
            ItemDTO item = itemMap.get(v.getItemId());
            if (item == null) {
                continue;
            }
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }
    
    @Override
    @Transactional
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                    .eq(Cart::getUserId, UserContext.getUser())
                    .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }
    
    /**
     * 检查购物车是否已满
     * todo 将购物车数量限制抽取到配置文件，实现配置热更新
     *
     * @param userId 用户id
     */
    private void checkCartsFull(Long userId) {
        int count = Math.toIntExact(lambdaQuery().eq(Cart::getUserId, userId).count());
        if (count >= cartProperties.getMaxItemCount()) {
            throw new BizIllegalException(StrUtil.format("用户购物车课程不能超过{}", cartProperties.getMaxItemCount()));
        }
    }
    
    private boolean checkItemExists(Long itemId, Long userId) {
        int count = Math.toIntExact(lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count());
        return count > 0;
    }
}
