package org.peanut.distributelock.lock;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 基于Redis的分布式锁实现
 * 实现AutoCloseable接口，实现TWR
 *
 * @author lch
 * @date 2020-12-23 18:47:28
 */
@Slf4j
public class RedisLock implements AutoCloseable {

    private RedisTemplate redisTemplate;

    private String key;
    private String value;
    /**
     * 单位：秒
     */
    private int expireTime;

    private final Consumer<Boolean> unlockHandler;

    public RedisLock(RedisTemplate redisTemplate, String key, int expireTime, Consumer<Boolean> unlockHandler) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.expireTime = expireTime;
        this.value = UUID.randomUUID().toString();
        this.unlockHandler = unlockHandler;
    }

    /**
     * 获取锁
     *
     * @return 获取成功
     */
    public Boolean getLock() {
        RedisCallback<Boolean> callback = redisConnection -> {
            // 设置NX
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            // 设置过期时间
            Expiration expiration = Expiration.seconds(expireTime);
            // 序列化key
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            // 序列化value
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            // 执行setnx操作
            Boolean result = redisConnection.set(redisKey, redisValue, expiration, setOption);
            return result;
        };
        Boolean lock = (Boolean) redisTemplate.execute(callback);
        return lock;
    }

    /**
     * 释放锁
     *
     * @return 释放成功
     */
    public Boolean unLock() {
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "  return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "  return 0\n" +
                "end";

        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        List<String> keys = Arrays.asList(key);

        Boolean result = (Boolean) redisTemplate.execute(redisScript, keys, value);
        return result;
    }

    @Override
    public void close() throws Exception {
        Boolean unLock = unLock();
        unlockHandler.accept(unLock);
    }
}
