package org.peanut.distributelock.service;

import lombok.extern.slf4j.Slf4j;
import org.peanut.distributelock.lock.ZkLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lch
 * @date 2020-12-24 16:07:40
 */
@Service
@Slf4j
public class OrderService {

    @Value("${zookeeper.connect.url}")
    private String connectString;

    private final AtomicInteger count = new AtomicInteger(0);

    public String createOrder() {
        try (ZkLock lock = new ZkLock(connectString, "demoKey", node -> {
            log.info("释放锁： " + node);
        })
        ) {
            if (lock.getLock()) {
                Thread.sleep(6_000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(count.incrementAndGet());
    }
}
