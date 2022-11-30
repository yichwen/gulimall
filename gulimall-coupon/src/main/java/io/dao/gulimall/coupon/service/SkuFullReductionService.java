package io.dao.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.to.SkuReductionTo;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 15:35:31
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

