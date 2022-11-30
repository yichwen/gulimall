package io.dao.gulimall.product.service.impl;

import io.dao.gulimall.product.vo.SkuItemSaleAttrVo;
import io.dao.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.product.dao.SkuSaleAttrValueDao;
import io.dao.gulimall.product.entity.SkuSaleAttrValueEntity;
import io.dao.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<SkuItemSaleAttrVo> vos = baseMapper.getSaleAttrsBySpuId(spuId);
        return vos;
    }

    @Override
    public List<String> getSkuSaleAttrValueAsStringList(Long skuId) {
        return baseMapper.getSkuSaleAttrValueAsStringList(skuId);
    }

}