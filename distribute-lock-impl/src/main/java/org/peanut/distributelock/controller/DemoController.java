package org.peanut.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lch
 * @date 2020-12-23 16:34:26
 */
@RestController
@Slf4j
public class DemoController {

    private Lock lock = new ReentrantLock();

    /**
     * 单体锁存在问题
     *
     * @return 结果
     */
    @RequestMapping("/singleLock")
    public String singleLock() {
        StopWatch watch = new StopWatch(UUID.randomUUID().toString());
        watch.start("获取锁");
        log.info("进入了方法！");
        lock.lock();
        try {
            log.info("进入了锁！");
            watch.stop();
            watch.start("执行业务代码");
            Thread.sleep(6_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        watch.stop();
        log.info(watch.prettyPrint());
        return "执行完成";
    }
}
