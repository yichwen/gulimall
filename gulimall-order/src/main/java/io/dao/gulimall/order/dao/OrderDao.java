package io.dao.gulimall.order.dao;

import io.dao.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:11:25
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    Long updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code);
}
