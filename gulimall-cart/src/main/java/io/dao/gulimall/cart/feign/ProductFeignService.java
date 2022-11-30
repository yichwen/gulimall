package io.dao.gulimall.cart.feign;

import io.dao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuInfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValue(@PathVariable Long skuId);

    // 如果有问题，就将响应数据改为 R，演示时，是有异常的
    @GetMapping("/product/skuInfo/{skuId}/price")
    BigDecimal getPrice(@PathVariable Long skuId);

}
