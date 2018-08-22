package com.aws.bq.zip.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePrefixPredicate;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/16/2018
 */
@Configuration
public class AmazonConfiguration {
    @Value("${amazon.s3.bucket}")
    private String BUCKET_NAME;

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();

        // 创建Amazon S3 生命周期设置，过期时间设置为30天
        BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule()
                .withId("expire 30 days")
                .withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("zip/")))
//                .addTransition(new Transition().withDays(30).withStorageClass(StorageClass.Glacier))
                .withExpirationInDays(30)
                .withStatus(BucketLifecycleConfiguration.ENABLED);

        BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration().withRules(rule);
        amazonS3.setBucketLifecycleConfiguration(BUCKET_NAME, configuration);
        return amazonS3;
    }

    @Bean
    public TransferManager transferManager() {
        TransferManager manager = TransferManagerBuilder.standard().build();
        return manager;
    }
}
