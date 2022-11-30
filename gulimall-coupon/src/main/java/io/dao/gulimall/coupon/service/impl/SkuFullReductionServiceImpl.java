package io.dao.gulimall.coupon.service.impl;

import io.dao.common.to.MemberPrice;
import io.dao.common.to.SkuReductionTo;
import io.dao.gulimall.coupon.entity.MemberPriceEntity;
import io.dao.gulimall.coupon.entity.SkuLadderEntity;
import io.dao.gulimall.coupon.service.MemberPriceService;
import io.dao.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.coupon.dao.SkuFullReductionDao;
import io.dao.gulimall.coupon.entity.SkuFullReductionEntity;
import io.dao.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // SKU的优惠，满减信息  sms_sku_ladder,sms_sku_full_reduction,sms_member_price,

        // sms_sku_ladder
        if (skuReductionTo.getFullCount() > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
            skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
            skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            this.skuLadderService.save(skuLadderEntity);
        }

        // sms_sku_full_reduction
        if (skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
            this.save(skuFullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntities = memberPrice.stream().map(price -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setMemberLevelId(price.getId());
            memberPriceEntity.setMemberLevelName(price.getName());
            memberPriceEntity.setMemberPrice(price.getPrice());
            return memberPriceEntity;
        })
                .filter(memberPriceEntity -> memberPriceEntity.getMemberPrice().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
        this.memberPriceService.saveBatch(memberPriceEntities);
    }

}