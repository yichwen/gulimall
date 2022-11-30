package io.dao.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.dao.common.utils.HttpUtils;
import io.dao.gulimall.member.dao.MemberLevelDao;
import io.dao.gulimall.member.entity.MemberLevelEntity;
import io.dao.gulimall.member.exception.PhoneExistException;
import io.dao.gulimall.member.exception.UsernameExistException;
import io.dao.gulimall.member.vo.SocialUser;
import io.dao.gulimall.member.vo.UserLoginVo;
import io.dao.gulimall.member.vo.UserRegistVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.member.dao.MemberDao;
import io.dao.gulimall.member.entity.MemberEntity;
import io.dao.gulimall.member.service.MemberService;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public void register(UserRegistVo vo) {
        MemberEntity entity = new MemberEntity();

        // 默认会员等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(memberLevelEntity.getId());

        validatePhoneUnique(vo.getPhone());
        validateUsernameUnique(vo.getUserName());

        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        entity.setPassword(encoder.encode(vo.getPassword()));

        baseMapper.insert(entity);
    }

    @Override
    public void validatePhoneUnique(String phone) throws PhoneExistException {
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void validateUsernameUnique(String username) throws UsernameExistException {
        Integer userCount = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (userCount > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(UserLoginVo vo) {
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
            .eq("username", vo.getLoginacct()).or().eq("mobile", vo.getLoginacct()));
        if (entity == null) {
            return null;
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(vo.getPassword(), entity.getPassword())) {
            return entity;
        }
        return null;
    }

    @Override
    public MemberEntity login(SocialUser vo) {
        // 登录和注册合并
        MemberEntity socialUser = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", vo.getUid()));
        if (socialUser != null) {
            // 登录
            MemberEntity updateEntity = new MemberEntity();
            updateEntity.setId(socialUser.getId());
            updateEntity.setAccessToken(vo.getAccess_token());
            updateEntity.setExpiresIn(String.valueOf(vo.getExpires_in()));
            baseMapper.updateById(updateEntity);

            socialUser.setAccessToken(vo.getAccess_token());
            socialUser.setExpiresIn(String.valueOf(vo.getExpires_in()));
            return socialUser;
        } else {
            // 注册
            MemberEntity memberEntity = new MemberEntity();
            Map<String, String> query = new HashMap<>();
            query.put("access_token", vo.getAccess_token());
            query.put("uid", vo.getUid());
            HttpResponse response = null;
            try {
                response = HttpUtils.doGet("api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    memberEntity.setNickname(name);
                    memberEntity.setGender("m".equals(gender) ? 1 : 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            memberEntity.setSocialUid(vo.getUid());
            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(String.valueOf(vo.getExpires_in()));
            baseMapper.insert(memberEntity);
            return memberEntity;
        }
    }

}