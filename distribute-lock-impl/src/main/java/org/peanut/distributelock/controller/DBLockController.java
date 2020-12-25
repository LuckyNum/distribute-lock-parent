package org.peanut.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.peanut.distributelock.dao.DistributeLockMapper;
import org.peanut.distributelock.model.DistributeLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author lch
 * @date 2020-12-23 18:06:37
 */
@RestController
@Slf4j
public class DBLockController {

    @Autowired
    private DistributeLockMapper distributeLockMapper;

    @RequestMapping("/dbLock")
    @Transactional(rollbackFor = Exception.class)
    public String dbLock() {
        log.info("进入了方法！");
        DistributeLock lock = distributeLockMapper.selectDistributeLock("demo");

        Optional.ofNullable(lock).orElseThrow(() -> new RuntimeException("分布式锁找不到"));

        log.info("进入了锁！");
        try {
            Thread.sleep(6_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "执行完成";
    }
}
