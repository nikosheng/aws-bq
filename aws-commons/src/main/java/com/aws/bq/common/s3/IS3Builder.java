package com.aws.bq.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;
import java.net.URL;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/22/2018
 */
public interface IS3Builder {
    /**
     * 上传数据
     * @param amazonS3
     * @param bucketName
     * @param key
     * @param file_path
     * @return
     */
    PutObjectResult putObject(AmazonS3 amazonS3, String bucketName, String key, String file_path);

    /**
     * 下载数据
     * @param amazonS3
     * @param bucketName
     * @param key
     * @return
     */
    File getObject(AmazonS3 amazonS3, String bucketName, String key);

    /**
     * 获取S3对象URL
     * @param amazonS3
     * @param bucket
     * @param key
     * @return
     */
    URL getUrl(AmazonS3 amazonS3, String bucket, String key);
}
