package io.dao.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import io.dao.common.utils.R;
import io.dao.gulimall.ware.feign.MemberFeignService;
import io.dao.gulimall.ware.vo.FareVo;
import io.dao.gulimall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.ware.dao.WareInfoDao;
import io.dao.gulimall.ware.entity.WareInfoEntity;
import io.dao.gulimall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.hasText(key)) {
            wrapper.eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.info(addrId);
        if (r.getCode() == 0) {
            MemberAddressVo address = r.getData("memberReceiveAddress", new TypeReference<>(){});
            fareVo.setAddress(address);
            if (address != null) {
                // 只是模拟计算运费
                String phone = address.getPhone();
                String substring = phone.substring(phone.length() - 1);
                fareVo.setFare(new BigDecimal(substring));
                return fareVo;
            }
        }
        return null;
    }

}