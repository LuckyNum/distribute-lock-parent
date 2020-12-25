package org.peanut.distributelock;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

/**
 * @author lch
 * @date 2020-12-24 15:24:58
 */
@SpringBootApplication
@ImportResource("classpath*:redisson.xml")
public class DistributeLockApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DistributeLockApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
