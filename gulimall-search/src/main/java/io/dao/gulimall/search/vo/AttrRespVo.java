package io.dao.gulimall.search.vo;

import lombok.Data;

@Data
public class AttrRespVo {

    private Long attrId;
    private String attrName;
    private Integer searchType;
    private String icon;
    private String valueSelect;
    private Integer attrType;
    private Integer valueType;
    private Long enable;
    private Long catelogId;
    private Integer showDesc;
    private Long attrGroupId;
    /**
     * 			"catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;
    // 用于属性详情
    private Long[] catelogPath;
}
