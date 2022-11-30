package io.dao.gulimall.product.vo;

import io.dao.gulimall.product.entity.SkuImagesEntity;
import io.dao.gulimall.product.entity.SkuInfoEntity;
import io.dao.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    private SkuInfoEntity info;
    private boolean hasStock = true;
    private List<SkuImagesEntity> images;
    private List<SkuItemSaleAttrVo> saleAttrs;
    private SpuInfoDescEntity desc;
    private List<SpuItemAttrGroupVo> groupAttrs;

//    @Data
//    public static class SkuItemSaleAttrVo {
//        private Long attrId;
//        private String attrName;
//        private List<AttrValueWithSkuIdVo> attrValues;
//    }
//
//    @Data
//    public static class AttrValueWithSkuIdVo {
//        private String attrValue;
//        private String skuIds;
//    }
//
//    @Data
//    public static class SpuItemAttrGroupVo {
//        private String groupName;
//        private List<SpuBaseAttrVo> attrs;
//    }
//
//    @Data
//    public static class SpuBaseAttrVo {
//        private String attrName;
//        private String attrValue;
//    }


}
