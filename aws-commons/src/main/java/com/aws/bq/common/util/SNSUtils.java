package com.aws.bq.common.util;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

/**
 * @Description: SNS 工具类
 * @author: jiasfeng
 * @Date: 8/13/2018
 */
public class SNSUtils {
    private static AmazonSNS amazonSNS = AmazonSNSClientBuilder.defaultClient();

    /**
     * 发送SNS消息
     * @param topicArn
     * @param message
     * @return
     */
    public static PublishResult sendMessage(String topicArn, String message) {
        return amazonSNS.publish(new PublishRequest(topicArn, message));
    }
}
