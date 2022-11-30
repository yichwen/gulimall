package io.dao.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 1. integrate mybatis-plus
 * 		- add denpendency mybatis-plus-boot-starter
 * 		- configuration
 * 			- configure data source
 * 				- add database driver dependency e.g. mysql-connector-java
 * 			 	- add configuration to application.yaml
 * 			- configure mybatis-plus
 * 				- use MapperScan
 * 				- tell mybatis-plus sql mapping file location
 *
 * 2. 逻辑删除
 * 	- 配置全局的逻辑删除规则 (optional)
 * 		see application.yaml:
 * 		mybatis-plus.global-config.db-config.logic-delete-value
 * 		mybatis-plus.global-config.db-config.logic-not-delete-value
 * 	- 配置逻辑删除的组件 bean (optional if version >= 3.1.1)
 * 	- 给 entity bean 加上逻辑删除注解 @TableLogic
 *
 * 3. JSR303
 * 	- 给 bean 添加校验注解（javax.validation.constraints.*），并定义自己的message提示
 * 		e.g. @NotEmpty(message="xxxx") private String name;
 * 	- 开启校验功能 @Valid， 效果：校验错误以后会有默认的相应
 * 		e.g. public void process(@Valid Person person) { ... }
 * 	- 给校验的 bean 后紧跟一个 BindingResult，就可以获得到校验的结果
 * 		e.g. public void process(@Valid Person person, BindingResult result) { ... }
 * 	- 分组校验（多场景的复杂校验）
 * 		- @NotNull(message = "修改必须指定品牌id", groups = { UpdateGroup.class })
 * 		- 给校验注解标注什么情况需要进行校验
 * 		- @Validated({AddGroup.class})
 * 		- 默认没有指定校验分组的校验注解 e.g. @NotBlank
 * 			- 在分组校验情况@Validated({AddGroup.class})下不生效，只会在@Validated生效
 * 	- 自定义校验
 * 		- 编写一个自定义的校验注解
 * 		- 编写一个自定义的校验器
 * 		- 关联自定义的校验器和自定义的校验注解
 * 	@Documented
 * 	// 这里可以指定多个校验器，适配不同类型的校验
 * 	@Constraint(validatedBy = { ListValueConstraintValidator.class })
 * 	@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
 * 	@Retention(RetentionPolicy.RUNTIME)
 * 	// 自定义注解
 * 	public @interface ListValue { ... }
 *
 * 4. 统一的异常处理（@ControllerAdvice）
 * 	- 编写异常处理类，使用@ControllerAdvice
 * 	- 使用@ExceptionHandler标注方法可以处理的异常
 *
 * 5. 模板引擎 thymeleaf
 *  - 导入 spring-boot-starter-thymeleaf 依赖
 *  - 关闭缓存 spring.thymeleaf.cache=false
 *  - 静态资源放在static下，可以按照路径直接访问
 *  - 页面放在templates下，可以直接访问
 *  - 页面修改不重启服务器实时更新
 *  	- 引入 spring-boot-devtools
 *  	- 修改完页面 ctrl+shift+f9 重新自动编译页面（代码配置的更改，推荐重启应用）
 *
 * 6. 整合redis
 * 	- 引入 spring-boot-starter-data-redis
 * 	- 简单配置 spring.redis.host, spring.redis.port
 * 	- 使用 spring boot 自动配置好的 StringRedisTemplate
 *
 * 7. 整合 redisson 作为所有分布式锁和分布式对象等功能的框架
 * 	- 引入 redisson
 *	- 配置 redisson MyRedissonConfig给容器中配置RedissonClient实例即可
 *  - 使用 参照文档
 *
 * 8. 整合 SpringCache 简化缓存开发
 * 	- 引入 spring-boot-starter-cache
 * 	- 写配置
 * 		1. 自动配置了哪些？
 * 		- CacheAutoConfiguration会导入RedisCacheAutoConfiguration
 * 		- 自动配好了 缓存管理器 CacheManager
 * 		2. 配置使用Redis作为缓存 spring.cache.type=redis
 * 		3. 测试使用缓存
 * 		- @Cacheable 保存数据到缓存的操作
 * 		- @CacheEvict 将数据从缓存中删除
 * 		- @CachePut	不影响方法执行更新操作
 * 		- @Caching 组合以上多个操作
 * 		- @CacheConfig 在类级别共享缓存配置
 * 			1. 开启缓存功能 @EnableCaching
 * 			2. 只需要使用注解就能完成缓存操作
 *		4. 原理
 *			CacheAutoConfiguration -> RedisCacheConfiguration -> 自动配置了 RedisCacheManager ->
 *			初始化所有的缓存 -> 每个缓存决定使用什么配置 -> 如果 redisCacheConfiguration有就拥有的，没有就用默认配置
 *			-> 想改缓存的配置，只需给容器中添加一个 RedisCacheConfiguration 即可 ->
 *			就会应用到当前 RedisCacheManager 管理的所有缓存分区中
 *	- Spring Cache 的不足
 *		- 读模式：
 *			缓存穿透：查询一个null数据。解决：缓存空数据（spring.cache.redis.cache-null-values=true）
 *			缓存击穿：大量并发进来同时查询一个正好过期的数据。解决：加锁（默认是不加锁的但可以设置 @Cacheable(sync=true) 来解决击穿）
 *			缓存雪崩：大量的key同时过期。解决：加随机时间，加过期时间（spring.cache.redis.time-to-live）
 *		- 写模式：（缓存与数据库的一致）
 *			读写加锁：读多写少的情况
 *			引入中间件 Canal：感知到mysql的更新数据库
 *			读多写多：直接访问数据库
 *		- 总结：
 *			常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用 spring cache；写模式：只要缓存数据有过期时间就足够了）
 *			特殊数据 (特殊设计）
 *		- 原理：
 *			CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
 *
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "io.dao.gulimall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {
	public static void main(String[] args) {
		SpringApplication.run(GulimallProductApplication.class, args);
	}

}
