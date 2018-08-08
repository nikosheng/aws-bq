package com.aws.bq.zip.config;

import com.aws.bq.contract.service.IContractService;
import com.aws.bq.contract.service.impl.ContractServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/8/2018
 */
@Configuration
public class ServiceConfiguration {
    @Bean
    public IContractService contractService() {
        return new ContractServiceImpl();
    }
}
