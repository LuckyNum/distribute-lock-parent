package org.peanut.distributedemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author lch
 * @date 2020-12-22 22:13:23
 */
@SpringBootApplication
@MapperScan("org.peanut.distributedemo.dao")
public class DistributeDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DistributeDemoApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
