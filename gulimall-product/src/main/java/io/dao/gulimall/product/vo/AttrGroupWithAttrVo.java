package io.dao.gulimall.product.vo;

import io.dao.gulimall.product.entity.AttrEntity;
import io.dao.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;
}
