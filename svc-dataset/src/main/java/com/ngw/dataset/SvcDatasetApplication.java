package com.ngw.dataset;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = { "com.ngw"})
@MapperScan({"com.ngw.**.mapper"})
@EnableAsync
public class SvcDatasetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvcDatasetApplication.class, args);
    }

}
