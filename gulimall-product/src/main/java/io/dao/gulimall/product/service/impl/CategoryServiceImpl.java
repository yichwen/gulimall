package io.dao.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.dao.gulimall.product.service.CategoryBrandRelationService;
import io.dao.gulimall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dao.common.utils.PageUtils;
import io.dao.common.utils.Query;

import io.dao.gulimall.product.dao.CategoryDao;
import io.dao.gulimall.product.entity.CategoryEntity;
import io.dao.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2. 组装成父子的树形结构
        // 2.1. 找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream()
                .filter(entity -> entity.getParentCid() == 0)
                .map(entity -> {
                    // 查找所有菜单的子菜单
                    entity.setChildren(getChildren(entity, entities));
                    return entity;
                })
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
        return level1Menus;
    }

    // 递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter(entity -> entity.getParentCid().equals(root.getCatId()))
                .map(entity -> {
                    entity.setChildren(getChildren(entity, all));
                    return entity;
                })
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO: 检查当前删除菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPaths = this.findParentPath(catelogId, paths);
        Collections.reverse(parentPaths);
        return parentPaths.toArray(new Long[0]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 级联更新所有数据
     *
     * @param category
     */
    @Transactional  //因为涉及到多次修改，因此要开启事务
    @Override
    // @CacheEvict 失效模式
    // 标识一个 key 删除其缓存
//    @CacheEvict(value = {"category"}, key = "'getLevel1Categories'")
    // 使用 @Caching 根据多个 keys 删除缓存
    @Caching(evict = {
            @CacheEvict(value = {"category"}, key = "'getLevel1Categories'"),
            @CacheEvict(value = {"category"}, key = "'getCatalogJson'")
    })
    // 删除缓存区下的所有数据
    @CacheEvict(value = {"category"}, allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category);
        }
    }

    // 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用，如果缓存中没有，会调用方法，最后将方法的结果放入缓存
    // 每个需要缓存的数据都来指定要放到哪个名字的缓存【缓存的分区】
    // key默认自动生成 value默认使用jdk序列化，将序列化的数据存到redis
    // 默认ttl是-1，自定义shi用配置文件 spring.cache.redis.time-to-live=3600000
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        // 将数据库的多次查询变为一次
        List<CategoryEntity> categories = baseMapper.selectList(null);
//        List<CategoryEntity> level1Categories = getLevel1Categories();
        List<CategoryEntity> level1Categories = getParentCid(categories, 0L);
        Map<String, List<Catalog2Vo>> map = level1Categories.stream()
                .collect(Collectors.toMap(v -> String.valueOf(v.getCatId()), l1 -> {
                    // 根据一级分类的id查询二级分类
                    List<CategoryEntity> level2Categories = getParentCid(categories, l1.getCatId());
                    // 转换为vo
                    List<Catalog2Vo> catelogV2Vos = null;
                    if (level2Categories != null) {
                        catelogV2Vos = level2Categories.stream().map(l2 -> {
                            // 根据二级分类的id查询三级分类
                            List<CategoryEntity> level3Categories = getParentCid(categories, l2.getCatId());
                            List<Catalog2Vo.Catalog3Vo> catelogV3Vos = null;
                            if (level3Categories != null) {
                                catelogV3Vos = level3Categories.stream().map(l3 -> {
                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(
                                            String.valueOf(l2.getCatId()),
                                            String.valueOf(l3.getCatId()),
                                            l3.getName()
                                    );
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                            }
                            Catalog2Vo vo = new Catalog2Vo(String.valueOf(l1.getCatId()),
                                    String.valueOf(l2.getCatId()), l2.getName(), catelogV3Vos);
                            return vo;
                        }).collect(Collectors.toList());
                    }
                    return catelogV2Vos;
                }));
        return map;
    }

    /**
     * 1. 空结果缓存，解决缓存穿透
     * 2. 设置过期时间（加随机值），解决缓存雪崩
     * 3. 加锁，解决缓存击穿
     */
//    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJsonV1() {
        // 加入缓存逻辑，缓存中存的数据是json字符串
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            // 缓存中没有，查询数据库
            // Option 1: getCatalogJsonFromDBWithLocalLock
            // Option 2: getCatalogJsonFromDBWithRedisLock
            // Option 3: getCatalogJsonFromDBWithRedissonLock
            Map<String, List<Catalog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedisLock();
//            catalogJson = JSON.toJSONString(catalogJsonFromDB);
//            stringRedisTemplate.opsForValue().set("catalogJson", catalogJson);
            return catalogJsonFromDB;
        }
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<>() {
        });
        return result;
    }

    /**
     * 缓存里面的数据如何和数据库保持一致
     * 缓存一致性问题：
     * 1. 双写模式（更新了数据库再更新缓存，会产生脏数据/数据一致性的问题）
     * 2. 失效模式（更新了就删除缓存中的数据，会产生脏数据/数据一致性的问题）
     * 经常更新的数据，是否要缓存？实时性高的数据，直接读数据库
     */
    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedissonLock() {
        // 锁的名字：锁的粒度，越细越块
        // 占分布式锁
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();
        // 加锁成功...执行业务
        Map<String, List<Catalog2Vo>> catalogJsonFromDB;
        try {
            catalogJsonFromDB = getCatalogJsonFromDB();
        } finally {
            lock.unlock();
        }
        return catalogJsonFromDB;
    }

    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisLock() {
        // 占分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,30, TimeUnit.SECONDS);
        if (lock != null && lock) {
            // 加锁成功...执行业务
            Map<String, List<Catalog2Vo>> catalogJsonFromDB;
            // 设置过期时间，必须和加锁是同步的
//            stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            try {
                catalogJsonFromDB = getCatalogJsonFromDB();
            } finally {
                // 删除锁，必须原子操作，使用lua脚本
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockValue)) {
//                stringRedisTemplate.delete("lock");
//            }
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            return catalogJsonFromDB;
        } else {
            // 加锁失败...重试...等待可以自旋的方式
            // 休眠200ms
            try { Thread.sleep(200); } catch (Exception e) { e.printStackTrace(); }
            return getCatalogJsonFromDBWithRedisLock();
        }
    }

    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithLocalLock() {

        // 只要是同一把锁，就能锁住需要这个锁的所有线程
        // 1. synchronized (this) spring boot 所有的组件在容器中都是单例的
        // TODO: 本地锁 synchronized, JUL(Lock), 在分布式情况下，必须使用分布式锁
        synchronized (this) {
            return getCatalogJsonFromDB();
        }
    }

    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        // 查询缓存先
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.hasText(catalogJson)) {
            return JSON.parseObject(catalogJson, new TypeReference<>() {});
        }

        // 将数据库的多次查询变为一次
        List<CategoryEntity> categories = baseMapper.selectList(null);

//        List<CategoryEntity> level1Categories = getLevel1Categories();
        List<CategoryEntity> level1Categories = getParentCid(categories, 0L);

        Map<String, List<Catalog2Vo>> map = level1Categories.stream()
                .collect(Collectors.toMap(v -> String.valueOf(v.getCatId()), l1 -> {
                    // 根据一级分类的id查询二级分类
                    List<CategoryEntity> level2Categories = getParentCid(categories, l1.getCatId());
                    // 转换为vo
                    List<Catalog2Vo> catelogV2Vos = null;
                    if (level2Categories != null) {
                        catelogV2Vos = level2Categories.stream().map(l2 -> {
                            // 根据二级分类的id查询三级分类
                            List<CategoryEntity> level3Categories = getParentCid(categories, l2.getCatId());
                            List<Catalog2Vo.Catalog3Vo> catelogV3Vos = null;
                            if (level3Categories != null) {
                                catelogV3Vos = level3Categories.stream().map(l3 -> {
                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(
                                            String.valueOf(l2.getCatId()),
                                            String.valueOf(l3.getCatId()),
                                            l3.getName()
                                    );
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                            }
                            Catalog2Vo vo = new Catalog2Vo(String.valueOf(l1.getCatId()),
                                    String.valueOf(l2.getCatId()), l2.getName(), catelogV3Vos);
                            return vo;
                        }).collect(Collectors.toList());
                    }
                    return catelogV2Vos;
                }));

        // 把数据放进缓存必须在锁中
        catalogJson = JSON.toJSONString(map);
        stringRedisTemplate.opsForValue().set("catalogJson", catalogJson);
        return map;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> categories, Long parentCid) {
        return categories.stream()
                .filter(category -> category.getParentCid().equals(parentCid))
                .collect(Collectors.toList());
//        baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l1.getCatId()));
    }

}