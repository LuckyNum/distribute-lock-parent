package org.peanut.distributelock;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author lch
 * @date 2020-12-25 11:40:34
 */
@SpringBootApplication
public class RedissonBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RedissonBootApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
