package io.dao.gulimall.product.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dao.common.valid.AddGroup;
import io.dao.common.valid.UpdateGroup;
import io.dao.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.dao.gulimall.product.entity.BrandEntity;
import io.dao.gulimall.product.service.BrandService;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.R;

import javax.validation.Valid;

/**
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 13:08:08
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);
        return R.ok().put("brand", brand);
    }

    @GetMapping("/infos")
    public R infos(@RequestParam List<Long> brandIds) {
        List<BrandEntity> brands = brandService.getBrands(brandIds);
        return R.ok().put("brands", brands);
    }

    /**
     * 保存（使用BindingResult处理数据校验的错误）
     * 建议：使用异常统一处理
     */
    @RequestMapping("/v1/save")
    public R save(@Valid @RequestBody BrandEntity brand, BindingResult result){
        if (result.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            result.getFieldErrors().forEach((error) -> {
                // 获取错误提示
                String message = error.getDefaultMessage();
                // 获取错误属性名
                String field = error.getField();
                map.put(field, message);
            });
            return R.error(400, "提交数据不合法").put("data", map);
        }
		brandService.save(brand);
        return R.ok();
    }

    /**
     * 保存（使用异常处理数据校验的错误）
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }

}
