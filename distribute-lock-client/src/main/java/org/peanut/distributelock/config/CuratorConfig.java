package org.peanut.distributelock.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lch
 * @date 2020-12-24 17:13:29
 */
@Configuration
public class CuratorConfig {

    @Value("${zookeeper.connect.url}")
    private String connectString;

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework getCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }
}
