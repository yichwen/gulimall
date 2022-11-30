package io.dao.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.product.entity.AttrEntity;
import io.dao.gulimall.product.entity.ProductAttrValueEntity;
import io.dao.gulimall.product.vo.AttrGroupRelationVo;
import io.dao.gulimall.product.vo.AttrGroupWithAttrVo;
import io.dao.gulimall.product.vo.AttrRespVo;
import io.dao.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 13:08:08
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);

    List<Long> selectSearchAttrs(List<Long> attrIds);
}

