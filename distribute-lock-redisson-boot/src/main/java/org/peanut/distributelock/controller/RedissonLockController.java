package org.peanut.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author lch
 * @date 2020-12-23 22:49:46
 */
@RestController
@Slf4j
public class RedissonLockController {

    @Autowired
    private RedissonClient client;

    @Value("${server.port}")
    private String port;

    @RequestMapping("/redissonLock")
    public String redissonLock() {
        log.info("进入了方法！");
        StopWatch watch = new StopWatch("server.port: " + port);
        watch.start();
        RLock lock = client.getLock("order");
        try {
            lock.lock(30, TimeUnit.SECONDS);
            log.info("获得了锁！");
            Thread.sleep(10_000L);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("释放了锁！");
            lock.unlock();
        }
        watch.stop();
        log.info("方法执行完成： {}", watch.prettyPrint());
        return "方法执行完成！";
    }
}
