package io.dao.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import io.dao.common.exception.NoStockException;
import io.dao.common.to.mq.OrderTo;
import io.dao.common.to.mq.StockDetailTo;
import io.dao.common.to.mq.StockLockedTo;
import io.dao.common.utils.R;
import io.dao.gulimall.ware.entity.WareOrderTaskDetailEntity;
import io.dao.gulimall.ware.entity.WareOrderTaskEntity;
import io.dao.gulimall.ware.feign.OrderFeignService;
import io.dao.gulimall.ware.feign.ProductFeignService;
import io.dao.gulimall.ware.service.WareOrderTaskDetailService;
import io.dao.gulimall.ware.service.WareOrderTaskService;
import io.dao.gulimall.ware.vo.OrderItemVo;
import io.dao.gulimall.ware.vo.OrderVo;
import io.dao.gulimall.ware.vo.SkuHasStockVo;
import io.dao.gulimall.ware.vo.WareStockLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.ware.dao.WareSkuDao;
import io.dao.gulimall.ware.entity.WareSkuEntity;
import io.dao.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WareOrderTaskService orderTaskService;

    @Autowired
    private WareOrderTaskDetailService orderTaskDetailService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private OrderFeignService orderFeignService;

    /**
     * 下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，锁定的库存需要自动解锁
     *
     * 只要解锁库存失败，一定要告诉服务器解锁失败
     */
    public void handleStockLockedRelease(StockLockedTo to) throws IOException {
        StockDetailTo detail = to.getDetail();
        Long detailId = detail.getId();
        // 查询数据库关于这个订单的锁定库存信息
        // 如果有数据，证明锁定成功，但是要不要解锁，需要看订单的情况
        //      没有订单，必须解锁，
        //      有订单，需要查看订单状态，已取消，解锁库存，没取消，不能解锁
        // 如果没有数据，库存锁定失败了，库存回滚了，这种情况无需解锁
        WareOrderTaskDetailEntity orderTaskDetailEntity = orderTaskDetailService.getById(detailId);
        if (orderTaskDetailEntity != null) {
            // 解锁
            Long id = to.getId();
            WareOrderTaskEntity orderTaskEntity = orderTaskService.getById(id);
            String orderSn = orderTaskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo order = r.getData(new TypeReference<>(){});
                // 订单关闭消息一定要比库存解锁消息块
                if (order == null || order.getStatus() == 4) {
                    // 订单不存在 或 订单已被取消的
                    if (detail.getLockStatus() == 1) {
                        // 当前库存工作单状态 已锁定才解锁
                        unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detail.getId());
                    }
                }
            } else {
                // 消息拒绝后重新放入队列，让别人消费解锁
                throw new RuntimeException("远程服务失败");
            }
        }
    }

    // 防止卡顿的订单无法解锁库存
    @Transactional
    @Override
    public void handleOrderCloseRelease(OrderTo to) {
        String orderSn = to.getOrderSn();
        WareOrderTaskEntity orderTask = orderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = orderTask.getId();
        // 按照工作单找到所有 没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> orderTaskDetailEntityList = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity orderTaskDetailEntity : orderTaskDetailEntityList) {
            unlockStock(orderTaskDetailEntity.getSkuId(),
                    orderTaskDetailEntity.getWareId(),
                    orderTaskDetailEntity.getSkuNum(),
                    orderTaskDetailEntity.getId());
        }
    }

    private void unlockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        // 库存解锁
        wareSkuDao.unlockStock(skuId, wareId, num);
        // 更新库存工作单的状态
        WareOrderTaskDetailEntity orderTaskDetailEntity = new WareOrderTaskDetailEntity();
        orderTaskDetailEntity.setId(taskDetailId);
        orderTaskDetailEntity.setLockStatus(2);
        orderTaskDetailService.updateById(orderTaskDetailEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (StringUtils.hasText(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.hasText(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities != null && wareSkuEntities.size() > 0) {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        } else {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            // TODO: 还有什么方法让异常出现后不回滚？
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    skuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {

            }
            wareSkuDao.insert(skuEntity);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(id -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = this.baseMapper.getSkuStock(skuIds);
            vo.setSkuId(id);
            vo.setHasStock(count != null && count > 0);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 库存解锁场景：
     * 1. 下订单成功，订单过期没有支付被系统自动取消，被用户手动取消，都要解锁库存
     * 2. 下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，锁定的库存需要自动解锁
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareStockLockVo vo) {
        // 按照下单的收货地址，找到一个就近仓库，锁定库存

        // 保存库存工作单的详情
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);

        // 找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> skuHashStockList = locks.stream().map(orderItem -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = orderItem.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareIds(wareIds);
            skuWareHasStock.setNum(orderItem.getCount());
            return skuWareHasStock;
        }).collect(Collectors.toList());

        boolean allLock = true;
        // 锁定库存
        for (SkuWareHasStock skuWareHasStock : skuHashStockList) {
            boolean skuStockLocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareIds();
            if (wareIds != null && wareIds.size() > 0) {
                // 如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给 MQ
                // 锁定失败，前面保存的工作单信息就回滚了，发送出去的消息，即使要解锁记录，由于去数据库查不到 id，所以就不用解锁
                for (Long wareId : wareIds) {
                    Long count = wareSkuDao.lockSkuStock(skuId, wareId, skuWareHasStock.getNum());
                    if (count == 1) {
                        // 锁定成功
                        skuStockLocked = true;
                        // 保存库存工作单的详情
                        WareOrderTaskDetailEntity orderTaskDetailEntity = new WareOrderTaskDetailEntity(
                          null, skuId, "", skuWareHasStock.getNum(), taskEntity.getId(), wareId, 1
                        );
                        orderTaskDetailService.save(orderTaskDetailEntity);
                        // 告诉 MQ 锁定成功
                        StockLockedTo stockLockedTo = new StockLockedTo();
                        stockLockedTo.setId(taskEntity.getId());
                        StockDetailTo stockDetailTo = new StockDetailTo();
                        BeanUtils.copyProperties(orderTaskDetailEntity, stockDetailTo);
                        // 只发 id 不行，防止回滚后找不到数据
                        stockLockedTo.setDetail(stockDetailTo);
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                        break;
                    }
                    // 锁定失败，尝试下一个仓库
                }
                if (!skuStockLocked) {
                    // 所有仓库都无法锁定库存，抛异常
                    throw new NoStockException(skuId);
                }
            } else {
                throw new NoStockException(skuId);
            }
        }

        // 全部锁定成功
        return true;

    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareIds; // 仓库 id
        private Integer num;
    }

}