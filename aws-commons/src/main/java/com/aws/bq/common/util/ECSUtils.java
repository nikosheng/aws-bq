package com.aws.bq.common.util;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import com.amazonaws.services.s3.model.Region;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description: ECS 工具类
 * @author: jiasfeng
 * @Date: 8/10/2018
 */
@Slf4j
public class ECSUtils {
    private static AmazonECS client =
            AmazonECSClientBuilder.standard().withRegion(Region.CN_Northwest_1.toString()).build();

    /**
     * 运行任务
     * @param request
     * @return
     */
    public static RunTaskResult runTask(RunTaskRequest request) {
        return client.runTask(request);
    }

    /**
     * 停止任务
     * @param cluster
     * @param tag
     * @return
     */
    public static boolean stopAllTask(String cluster, String tag) {
        // List the running task with tag
        try {
            ListTasksRequest listReq = new ListTasksRequest().withCluster(cluster)
                    .withStartedBy(tag);
            ListTasksResult result = client.listTasks(listReq);
            List<String> taskArns = result.getTaskArns();

            // Stop the task recursively with task ARN
            for (String taskArn : taskArns) {
                stopTask(cluster, taskArn);
            }
        } catch (Exception e) {
            log.error("[ECSUtils] ==========> Exception: ", e);
            return false;
        }
        return true;
    }

    /**
     * 停止任务
     * @param cluster
     * @param taskArn
     * @return
     */
    public static StopTaskResult stopTask(String cluster, String taskArn) {
        StopTaskRequest stopReq = new StopTaskRequest().withCluster(cluster)
                .withTask(taskArn);
        return client.stopTask(stopReq);
    }
}
