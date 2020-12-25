package org.peanut.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author lch
 * @date 2020-12-24 15:25:52
 */
@RestController
@Slf4j
public class ZookeeperController {

    @Autowired
    private CuratorFramework client;

    @Value("${server.port}")
    private String port;

    @RequestMapping("/curatorLock")
    public String curatorLock() {
        log.info("进入了方法！");
        StopWatch watch = new StopWatch("server.port: " + port);
        watch.start();
        InterProcessMutex lock = new InterProcessMutex(client, "/order");
        try {
            if (lock.acquire(30, TimeUnit.SECONDS)) {
                log.info("获得了锁！");
                Thread.sleep(10_000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                log.info("释放了锁！");
                lock.release();
            } catch (Exception e) {
                log.info("释放锁失败！");
                e.printStackTrace();
            }
        }
        watch.stop();
        log.info("方法执行完成: {}", watch.prettyPrint());
        return "方法执行完成！";
    }
}
