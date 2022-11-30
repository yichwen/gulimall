package io.dao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 13:08:08
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {
    PageUtils queryPage(Map<String, Object> params);
    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

