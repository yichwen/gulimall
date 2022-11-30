package io.dao.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dao.gulimall.product.entity.BrandEntity;
import io.dao.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

//    @Autowired
//    BrandService brandService;

	@Test
	void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("HuaWei");
//        brandService.save(brandEntity);
//        System.out.println("Save successfully..");

//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("HuaWei");
//        brandService.updateById(brandEntity);
//        System.out.println("Update successfully..");

//        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
//        list.forEach(System.out::println);
    }

    @Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void testRedis() {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set("hello", "world_"+ UUID.randomUUID().toString());
		String hello = ops.get("hello");
		System.out.println(hello);
	}

	@Autowired
	private RedissonClient redissonClient;

	@Test
	public void testRedisson() {
		System.out.println(redissonClient);
	}



}
