package com.zayn.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zayn.item.domain.po.Item;
import com.zayn.item.mapper.ItemMapper;
import com.zayn.item.service.IItemService;
import com.zayn.common.exception.BizIllegalException;
import com.zayn.common.utils.BeanUtils;
import com.zayn.api.dto.ItemDTO;
import com.zayn.api.dto.OrderDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {
    
    @Override
    @Transactional
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.heima.item.mapper.ItemMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("更新库存异常，可能是库存不足!", e);
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
    }
    
    @Override
    @Transactional
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }
    
    /**
     * 恢复库存
     *
     * @param orderDetailDTOS
     */
    @Override
    @Transactional
    public void recoverStock(List<OrderDetailDTO> orderDetailDTOS) {
        for (OrderDetailDTO dto : orderDetailDTOS) {
            Item item = lambdaQuery().eq(Item::getId, dto.getItemId()).one();
            lambdaUpdate()
                    .eq(Item::getId, dto.getItemId())
                    .set(Item::getStock, item.getStock() + dto.getNum())
                    .update();
        }
    }
}
