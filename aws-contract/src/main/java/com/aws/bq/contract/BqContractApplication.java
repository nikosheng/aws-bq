package com.aws.bq.contract;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/8/2018
 */
@SpringBootApplication
@MapperScan("com.aws.bq.contract.dao")
public class BqContractApplication {
    public static void main(String[] args) {
        SpringApplication.run(BqContractApplication.class, args);
    }
}
