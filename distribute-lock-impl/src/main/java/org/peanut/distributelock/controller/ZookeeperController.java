package org.peanut.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.peanut.distributelock.lock.RedisLock;
import org.peanut.distributelock.lock.ZkLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lch
 * @date 2020-12-24 11:41:21
 */
@RestController
@Slf4j
public class ZookeeperController {

    @Value("${zookeeper.connect.url}")
    private String connectString;

    @RequestMapping("/zookeeperLock")
    public String zookeeperLock() {
        log.info("进入了方法！");
        try (
                ZkLock lock = new ZkLock(connectString, "demoKey", node -> {
                    log.info("释放锁： " + node);
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
