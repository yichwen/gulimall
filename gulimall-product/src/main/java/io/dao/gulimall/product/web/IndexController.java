package io.dao.gulimall.product.web;

import io.dao.gulimall.product.entity.CategoryEntity;
import io.dao.gulimall.product.service.CategoryService;
import io.dao.gulimall.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 查出一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");
        // 加锁
//        lock.lock();        // 阻塞式等待，默认加的锁都是30s时间
        // Redisson解决的问题
        // 1. 锁的自动续期，如果业务超长，运行期间自动给锁续上新的周期（30s）
        // 2. 加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，默认在30s以后自动删除

        // 自定义解锁时间
        lock.lock(10, TimeUnit.SECONDS);    // 自动解锁时间一定呀大于业务的执行时间
        // 问题 lock.lock(10, TimeUnit.SECONDS); 在锁时间到了以后，不会自动续期
        // 如果传递了锁的超时时间，就发送给redis执行脚本，进行占领，默认超时就是我们指定的时间
        // 如果未指定锁的超时时间，就是用 30*1000【LockedWatchdogTimeout看门狗默认时间】
        //   只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗默认事件】
        //   internalLockLeaseTime【看门狗时间】/ 3 大约为 10s
        //   每隔10s都会自动再次续期，续成30s

        // 最佳实战
        // lock.lock(10, TimeUnit.SECONDS);   // 省掉了整个续期操作

        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch(Exception e) {

        } finally {
            // 解锁       假设解锁代码没有运行，redisson会不会出现死锁
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    // 第162-163课：读写锁测试
    // 读写锁保证一定能读到最新的数据，修改期间，写锁是一个互斥锁，读锁是一个共享锁
    // 写锁没释放，读就必须等待
    // 读 + 读 相当于无锁，并发读只会在redis中记录好，所有当前的读锁，都会同时加锁成功
    // 读 + 写 等待读锁释放
    // 写 + 写 阻塞方式
    // 写 + 读 等待写锁释放
    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        try {
            // 改数据加写锁，读数据加读锁
            lock.writeLock().lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
        return s;
    }


    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        lock.readLock().lock();
        try {
            return stringRedisTemplate.opsForValue().get("writeValue");
        } finally {
            lock.readLock().unlock();
        }
    }

    // 第164课：闭锁测试
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 等待闭锁都完成
        door.trySetCount(5);
        door.await();
        return "放假了...";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();  // 技术减一
        return id + "班的人都走了...";
    }

    // 第165课：信号量测试
    // semaphore 信号量可以用于分布式限流
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        // initialize the "park" to 3
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire(); // 阻塞方式，获取一个信号/值
        return "ok";
    }

    @ResponseBody
    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release(); // 释放一个信号/值
        return "go";
    }

}
