package com.aws.bq.common.util;

import com.aws.bq.common.s3.S3Client;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: S3 工具类
 * @author: jiasfeng
 * @Date: 8/10/2018
 */
@Slf4j
public class S3Utils {
    public static S3Client getS3Client() {
        return S3Client.build();
    }
}
