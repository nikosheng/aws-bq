package com.aws.bq.ecs;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
public interface IECSOperation {
    void stopTask(String cluster, String taskArn);
    void stopAllTask(String cluster, String tag);
}
