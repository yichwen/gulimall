package io.dao.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbitmq.client.Channel;
import io.dao.common.to.mq.OrderTo;
import io.dao.common.to.mq.StockLockedTo;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.ware.entity.WareSkuEntity;
import io.dao.gulimall.ware.vo.LockStockResultVo;
import io.dao.gulimall.ware.vo.SkuHasStockVo;
import io.dao.gulimall.ware.vo.WareStockLockVo;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:10:18
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareStockLockVo vo);

    void handleStockLockedRelease(StockLockedTo to) throws IOException;

    void handleOrderCloseRelease(OrderTo to);
}

