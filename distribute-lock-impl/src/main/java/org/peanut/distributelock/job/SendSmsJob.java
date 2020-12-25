package org.peanut.distributelock.job;

import lombok.extern.slf4j.Slf4j;
import org.peanut.distributelock.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author lch
 * @date 2020-12-23 22:56:06
 */
@Component
@Slf4j
public class SendSmsJob {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 5s 发送一次短信
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSms() {
        try (
                RedisLock lock = new RedisLock(redisTemplate, "sendSmsKey", 30, isSuccess -> {
                    if (isSuccess) {
                        log.info("自定义 - 释放锁成功");
                    } else {
                        log.info("自定义 - 释放锁失败");
                    }
                })
        ) {
            if (lock.getLock()) {
                log.info("发送短信 到 10086");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
