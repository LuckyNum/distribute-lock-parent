package org.peanut.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.peanut.distributelock.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lch
 * @date 2020-12-23 22:49:46
 */
@RestController
@Slf4j
public class RedisLockController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/redisLock")
    public String redisLock() {
        log.info("进入了方法！");
        try (
                RedisLock lock = new RedisLock(redisTemplate, "demoKey", 30, isSuccess -> {
                    if (isSuccess) {
                        log.info("自定义 - 释放锁成功");
                    } else {
                        log.info("自定义 - 释放锁失败");
                    }
                })
        ) {
            if (lock.getLock()) {
                log.info("进入了锁！");
                Thread.sleep(15_000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "执行完成";
    }
}
