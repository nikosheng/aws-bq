package com.aws.bq.ecs.impl;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.ListTasksRequest;
import com.amazonaws.services.ecs.model.ListTasksResult;
import com.amazonaws.services.ecs.model.StopTaskRequest;
import com.aws.bq.ecs.IECSOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@Service
public class ECSOperationImpl implements IECSOperation {
    @Autowired
    private AmazonECS client;

    @Override
    public void stopAllTask(String cluster, String tag) {
        // List the running task with tag
        ListTasksRequest listReq = new ListTasksRequest().withCluster(cluster)
                .withStartedBy(tag);
        ListTasksResult result = client.listTasks(listReq);
        List<String> taskArns = result.getTaskArns();

        // Stop the task recursively with task ARN
        for (String taskArn : taskArns) {
            stopTask(cluster, taskArn);
        }
    }

    @Override
    public void stopTask(String cluster, String taskArn) {
        StopTaskRequest stopReq = new StopTaskRequest().withCluster(cluster)
                .withTask(taskArn);
        client.stopTask(stopReq);
    }
}
