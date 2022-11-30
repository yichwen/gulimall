package io.dao.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.ware.entity.WareInfoEntity;
import io.dao.gulimall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:10:17
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

