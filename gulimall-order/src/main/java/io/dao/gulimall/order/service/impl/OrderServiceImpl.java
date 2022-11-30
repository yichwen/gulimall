package io.dao.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.dao.common.exception.NoStockException;
import io.dao.common.to.mq.OrderTo;
import io.dao.common.utils.R;
import io.dao.common.vo.MemberRespVo;
import io.dao.gulimall.order.entity.OrderItemEntity;
import io.dao.gulimall.order.entity.PaymentInfoEntity;
import io.dao.gulimall.order.enume.OrderStatusEnum;
import io.dao.gulimall.order.feign.CartFeignService;
import io.dao.gulimall.order.feign.MemberFeignService;
import io.dao.gulimall.order.feign.ProductFeignService;
import io.dao.gulimall.order.feign.WareFeignService;
import io.dao.gulimall.order.interceptor.LoginUserInterceptor;
import io.dao.gulimall.order.service.OrderItemService;
import io.dao.gulimall.order.service.PaymentInfoService;
import io.dao.gulimall.order.to.OrderCreateTo;
import io.dao.gulimall.order.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.order.dao.OrderDao;
import io.dao.gulimall.order.entity.OrderEntity;
import io.dao.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {

        MemberRespVo loginUser = LoginUserInterceptor.loginUser.get();
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();

        // ThreadLocal 数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            // 异步使用的线程不一样，所以必须重新设置，共享数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 远程查询所有的收货地址列表
            List<MemberAddressVo> addresses = memberFeignService.getAddress(loginUser.getId());
            orderConfirmVo.setAddress(addresses);
        }, executor);

        CompletableFuture<Void> orderItemFuture = CompletableFuture.runAsync(() -> {
            // 异步使用的线程不一样，所以必须重新设置，共享数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 远程查询购物车所有选中的购物项
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(items);
            // feign 在远程调用之前要构造请求，调用很多的拦截器 RequestInterceptor
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            List<SkuHasStockVo> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            if (skuHasStock != null && skuHasStock.size() > 0) {
                Map<Long, Boolean> stocks = skuHasStock.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::isHasStock));
                orderConfirmVo.setStocks(stocks);
            }
        }, executor);

        // 查询用户级分
        Integer integration = loginUser.getIntegration();
        orderConfirmVo.setIntegration(integration);

        // 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setOrderToken(token);
        redisTemplate.opsForValue().set("order:token" + loginUser.getId(), token, 30, TimeUnit.MINUTES);

        try {
            CompletableFuture.allOf(addressFuture, orderItemFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return orderConfirmVo;
    }

    // 提交订单属于高并发请求，seata AT 不适用
    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        MemberRespVo loginUser = LoginUserInterceptor.loginUser.get();
        confirmVoThreadLocal.set(vo);
        // 验证临牌
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end)";
        String orderToken = vo.getOrderToken();
        String key = "order:token" + loginUser.getId();
        // 令牌的对比和删除必须保证原子性
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(key), orderToken);
        // 返回数据 0 - 失败 / 1 - 成功
        if (result != null && result == 1L) {
            OrderCreateTo order = createOrder();
            BigDecimal newPayPrice = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(newPayPrice.subtract(payPrice).doubleValue()) < 0.01) {
                // 保存订单
                saveOrder(order);
                // 库存锁定，只要有异常回滚订单数据
                WareStockLockVo stockLockVo = new WareStockLockVo();
                stockLockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                stockLockVo.setLocks(locks);
                R r = wareFeignService.orderLockStock(stockLockVo); // 远程锁库存
                if (r.getCode() == 0) {
                    // 为了保证高并发，库存服务自己回滚，可以发送消息给库存服务
                    // 库存本身也可以使用自动解锁 （使用消息队列）
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    // 库存锁定成功
                    responseVo.setOrder(order.getOrder());
                } else {
                    // 库存锁定失败
                    String msg = r.getData("msg", new TypeReference<>(){});
                    throw new NoStockException(msg);
//                    responseVo.setCode(3);
                }
            } else {
                responseVo.setCode(2);
            }
        } else {
            responseVo.setCode(1);
        }
        return responseVo;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public void closeOrder(OrderEntity order) {
        // 查询当前这个订单的最新状态
        OrderEntity orderEntity = this.getById(order.getId());
        if (orderEntity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            OrderEntity updatedOrderEntity = new OrderEntity();
            updatedOrderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            updatedOrderEntity.setId(orderEntity.getId());
            this.updateById(updatedOrderEntity);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            try {
                // TODO: 保证消息一定会发送出去，每一个消息都可以做好日志记录（给数据库保存每一个消息的详细信息）
                // TODO: 定期扫描数据库，将失败的消息再发送
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                // TODO: 将没法发送成功的消息进行重试发送
            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        PayVo payVo = new PayVo();
        payVo.setBody("备注");    // 订单备注
        payVo.setSubject("谷粒商城");     // 订单主题
        payVo.setOut_trade_no(order.getOrderSn());    // 订单号
        payVo.setTotal_amount(order.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString());    // 订单金额
        return payVo;
    }

    @Override
    public String handleAlipayNotification(PayAsyncVo vo) {
        // 保存交易流水
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();

        paymentInfoService.save(paymentInfo);
        //
        String tradeStatus = vo.getTrade_status();
        if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
            String orderSn = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setCreateTime(new Date());
        this.save(orderEntity);

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder() {

        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        MemberRespVo loginUser = LoginUserInterceptor.loginUser.get();

        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 生成订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(loginUser.getId());
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);

        // 获取收货地址
        R r = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fare = r.getData(new TypeReference<>(){});

        entity.setFreightAmount(fare.getFare());

        entity.setReceiverCity(fare.getAddress().getCity());
        entity.setReceiverDetailAddress(fare.getAddress().getDetailAddress());
        entity.setReceiverName(fare.getAddress().getName());
        entity.setReceiverPhone(fare.getAddress().getPhone());
        entity.setReceiverPostCode(fare.getAddress().getPostCode());
        entity.setReceiverProvince(fare.getAddress().getProvince());
        entity.setReceiverRegion(fare.getAddress().getRegion());

        // 获取所有的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);

        // 计算价格
        computePrice(entity, orderItemEntities);

        orderCreateTo.setOrderItems(orderItemEntities);
        orderCreateTo.setOrder(entity);

        return orderCreateTo;
    }

    private void computePrice(OrderEntity entity, List<OrderItemEntity> orderItemEntities) {

        Integer growth = 0;
        Integer integration = 0;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal promotionAmount = BigDecimal.ZERO;
        BigDecimal couponAmount = BigDecimal.ZERO;
        BigDecimal integrationAmount = BigDecimal.ZERO;

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total = total.add(orderItemEntity.getRealAmount());
            promotionAmount = promotionAmount.add(orderItemEntity.getPromotionAmount());
            couponAmount = couponAmount.add(orderItemEntity.getCouponAmount());
            integrationAmount = integrationAmount.add(orderItemEntity.getIntegrationAmount());

            growth += orderItemEntity.getGiftGrowth();
            integration += orderItemEntity.getGiftIntegration();
        }
        entity.setTotalAmount(total);
        // 应付总额
        entity.setPayAmount(total.add(entity.getFreightAmount()));
        entity.setPromotionAmount(promotionAmount);
        entity.setCouponAmount(couponAmount);
        entity.setIntegrationAmount(integrationAmount);
        entity.setIntegration(integration);
        entity.setGrowth(growth);
    }

    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            return currentUserCartItems.stream().map(item -> {
                OrderItemEntity itemEntity = buildOrderItem(item);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        // 订单信息
        // 商品 sku 信息
        Long skuId = item.getSkuId();
        itemEntity.setSkuId(skuId);
        itemEntity.setSkuName(item.getTitle());
        itemEntity.setSkuPic(item.getImage());
        itemEntity.setSkuPrice(item.getPrice());
        itemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";"));
        itemEntity.setSkuQuantity(item.getCount());
        // 商品 spu 信息

        R r = productFeignService.spuInfoBySkuId(skuId);
        SpuInfoVo spuInfo = r.getData(new TypeReference<>(){});
        itemEntity.setSpuBrand(spuInfo.getBrandId().toString());
        itemEntity.setSpuId(spuInfo.getId());
        itemEntity.setSpuName(spuInfo.getSpuName());
//        itemEntity.setSpuPic(spuInfo.get);
        itemEntity.setCategoryId(spuInfo.getCatalogId());

        // 积分 成长值
        itemEntity.setGiftGrowth(item.getPrice().intValue() * item.getCount());
        itemEntity.setGiftIntegration(item.getPrice().intValue() * item.getCount());

        itemEntity.setPromotionAmount(BigDecimal.ZERO);
        itemEntity.setPromotionAmount(BigDecimal.ZERO);
        itemEntity.setIntegrationAmount(BigDecimal.ZERO);
        // 需要减掉以上的优惠金额
        itemEntity.setRealAmount(itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString())));

        return itemEntity;
    }

}