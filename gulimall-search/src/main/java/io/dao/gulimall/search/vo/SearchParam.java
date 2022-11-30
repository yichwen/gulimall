package io.dao.gulimall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件：
     * sort=skuPrice_asc/desc   （价格）
     * sort=saleCount_asc/desc  （销量）
     * sort=hotScore_desc/asc   （热度评分）
     */
    private String sort;

    /**
     * 是否显示有货 hasStock=0/1
     */
    private Integer hasStock;

    /**
     * 价格区间查询 skuPrice=1_500/_500/500_
     */
    private String skuPrice;

    /**
     * 品牌id,可以多选
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选 attrs=1_option1:option2
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;

}