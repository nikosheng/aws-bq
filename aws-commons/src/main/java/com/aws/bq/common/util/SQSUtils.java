package com.aws.bq.common.util;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description: SQS 工具类
 * @author: jiasfeng
 * @Date: 8/10/2018
 */
@Slf4j
public class SQSUtils {
    private static AmazonSQS amazonSQS = AmazonSQSClientBuilder.defaultClient();

    public static SendMessageResult sendMessage(String queueUrl, String message) {
        return amazonSQS.sendMessage(new SendMessageRequest(queueUrl, message));
    }

    public static List<Message> receiveMessage(String queueUrl) {
        return amazonSQS.receiveMessage(new ReceiveMessageRequest(queueUrl)).getMessages();
    }

    public static DeleteMessageResult deleteMessage(String queueUrl, String receiptHandle) {
        return amazonSQS.deleteMessage(new DeleteMessageRequest(queueUrl, receiptHandle));
    }
}
