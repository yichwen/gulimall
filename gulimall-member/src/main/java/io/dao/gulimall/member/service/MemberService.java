package io.dao.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.member.entity.MemberEntity;
import io.dao.gulimall.member.exception.PhoneExistException;
import io.dao.gulimall.member.exception.UsernameExistException;
import io.dao.gulimall.member.vo.SocialUser;
import io.dao.gulimall.member.vo.UserLoginVo;
import io.dao.gulimall.member.vo.UserRegistVo;

import java.util.Map;

/**
 * 
 *
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:09:06
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegistVo vo);

    void validatePhoneUnique(String phone) throws PhoneExistException;

    void validateUsernameUnique(String username) throws UsernameExistException;

    MemberEntity login(UserLoginVo vo);

    MemberEntity login(SocialUser vo);
}

