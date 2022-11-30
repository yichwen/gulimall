package io.dao.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:09:05
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

