package io.dao.gulimall.product.service.impl;

import io.dao.common.constant.ProductConstant;
import io.dao.common.to.SkuEsModel;
import io.dao.common.to.SkuReductionTo;
import io.dao.common.to.SpuBoundTo;
import io.dao.common.utils.R;
import io.dao.gulimall.product.entity.*;
import io.dao.gulimall.product.feign.CouponFeignService;
import io.dao.gulimall.product.feign.SearchFeignService;
import io.dao.gulimall.product.feign.WareFeignService;
import io.dao.gulimall.product.service.*;
import io.dao.gulimall.product.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    // Seata AT 分布式事务（适用于无高并发的请求）
    @GlobalTransactional
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {
        // 保存SPU基本信息 psm_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 保存SPU的描述图片 psm_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        if (decript != null && decript.size() >  0) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
            spuInfoDescEntity.setDecript(String.join(",", decript));
            this.spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        }

        // 保存SPU的图片集 pms_spu_images
        List<String> images = spuInfo.getImages();
        if (images != null && images.size() > 0) {
            this.spuImagesService.saveImages(spuInfoEntity.getId(), images);
        }

        // 保存SPU的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity byId = this.attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        this.productAttrValueService.saveProductAttr(productAttrValueEntities);

        // 保存SPU的积分信息 sms_spu_bounds
        Bounds bounds = spuInfo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = this.couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存SPU积分信息失败");
        }

        // 保存当前SPU对应的所有SKU信息
        List<Skus> skus = spuInfo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                String defaultImg = "";
               for(Images img: sku.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }
                // SKU的基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                // SKU的图片信息 pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(skuImage -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(skuImage.getImgUrl());
                    skuImagesEntity.setDefaultImg(skuImage.getDefaultImg());
                    return skuImagesEntity;
                })
                        .filter(skuImageEntity -> StringUtils.hasText(skuImageEntity.getImgUrl()))
                        .collect(Collectors.toList());
                // TODO: 没有图片路径的无需保存
                this.skuImagesService.saveBatch(imagesEntities);

                // SKU的销售属性信息 pms_sku_sale_attr_value
                List<Attr> skuAttrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSalesAttrValueEntities = skuAttrs.stream().map(skuAttr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(skuAttr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                this.skuSaleAttrValueService.saveBatch(skuSalesAttrValueEntities);

                // SKU的优惠，满减信息  sms_sku_ladder,sms_sku_full_reduction,sms_member_price,
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存SKU优惠信息失败");
                    }
                }
            });
        }
    }

    private void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.hasText(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.hasText(brandId) && !brandId.equalsIgnoreCase("0")) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.hasText(catelogId) && !catelogId.equalsIgnoreCase("0")) {
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // 查询当前sku的所有可以被检索规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream()
                .map(ProductAttrValueEntity::getAttrId)
                .collect(Collectors.toList());

        List<Long> indexAttrIds = attrService.selectSearchAttrs(attrIds);
        Set<Long> idSet = new HashSet<>(indexAttrIds);
        // 过滤可检索属性
        List<SkuEsModel.Attr> filteredAttrs = productAttrValueEntities.stream()
                .filter(e -> idSet.contains(e.getAttrId()))
                .map(e -> {
                    SkuEsModel.Attr attr = new SkuEsModel.Attr();
                    BeanUtils.copyProperties(e, attr);
                    return attr;
                })
                .collect(Collectors.toList());

        // 发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> hasStockMap = null;
        try {
            List<SkuHasStockVo> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            hasStockMap = skuHasStock.stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::isHasStock));
        } catch (Exception e) {
            log.error("库存服务查询有异常，原因{}", e);
        }

        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsModel> upProducts = skuInfoEntities.stream().map(e -> {

            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(e, skuEsModel);
            skuEsModel.setSkuPrice(e.getPrice());
            skuEsModel.setSkuImg(e.getSkuDefaultImg());

            // 是否有库存
            skuEsModel.setHasStock(finalHasStockMap == null ? true : finalHasStockMap.get(e.getSkuId()));

            // TODO: 热度评分
            skuEsModel.setHotScore(0L);

            // 查询品牌和分类的名字
            BrandEntity brand = brandService.getById(e.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(e.getCatalogId());
            skuEsModel.setCatalogName(category.getName());

            // 设置检索属性
            skuEsModel.setAttrs(filteredAttrs);

            return skuEsModel;
        }).collect(Collectors.toList());

        // TODO: 发送给ES进行保存
        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode() == 0) {
            // 远程调用成功
            // 修改当前SPU状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败
            // TODO: 重复调用，接口幂等性
        }
    }

    @Override
    public SpuInfoEntity spuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return this.getById(skuInfo.getSpuId());
    }


}