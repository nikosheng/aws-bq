package com.aws.bq.contract.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/14/2018
 */
@Configuration
@ConfigurationProperties(prefix = "threadpool")
public class ExecutorPoolConfiguration {

    /**
     * 核心线程池数
     */
    @Value("${threadpool.core-pool-size}")
    private int corePoolSize;

    /**
     * 最大线程
     */
    @Value("${threadpool.max-pool-size}")
    private int maxPoolSize;

    /**
     * 队列容量
     */
    @Value("${threadpool.queue-capacity}")
    private int queueCapacity;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    @Value("${threadpool.keep-alive-seconds}")
    private int keepAliveSeconds;


    @Bean(name="threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(keepAliveSeconds);
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return pool;
    }
}
