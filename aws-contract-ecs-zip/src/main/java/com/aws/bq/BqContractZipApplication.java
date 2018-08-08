package com.aws.bq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aws.bq.dao")
public class BqContractZipApplication {

    public static void main(String[] args) {
        SpringApplication.run(BqContractZipApplication.class, args);
    }
}
