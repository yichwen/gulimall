package io.dao.gulimall.search.vo;

import lombok.Data;

@Data
public class BrandVo {

    private Long brandId;
    private String name;
    private String logo;
    private String descript;
    private Integer showStatus;
    private String firstLetter;
    private Integer sort;
}
