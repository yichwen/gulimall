package io.dao.gulimall.order.vo;

import io.dao.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;   // 0-成功 错误状态码
}
