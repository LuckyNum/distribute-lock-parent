package org.peanut.distributelock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lch
 * @date 2020-12-23 16:32:48
 */
@SpringBootApplication
@MapperScan("org.peanut.distributelock.dao")
@EnableScheduling
public class DistributeLockApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DistributeLockApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
