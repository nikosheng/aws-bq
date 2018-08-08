package com.aws.bq.zip.config;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
@Configuration
public class AWSConfiguration {

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.defaultClient();
    }

    @Bean
    public AmazonECS amazonECS() {
        return AmazonECSClientBuilder.standard().withRegion(Region.CN_Northwest_1.toString()).build();
    }
}
