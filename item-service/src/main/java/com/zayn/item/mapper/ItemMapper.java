package com.zayn.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zayn.item.domain.po.Item;
import com.zayn.api.dto.OrderDetailDTO;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 */
public interface ItemMapper extends BaseMapper<Item> {
    
    @Update("UPDATE item SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);
    
    @Update("update  item set stock = stock + #{num} where id = #{itemId}")
    void recoverStock(OrderDetailDTO orderDetail);
}
