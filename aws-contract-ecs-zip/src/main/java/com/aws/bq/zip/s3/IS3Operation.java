package com.aws.bq.zip.s3;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.aws.bq.common.model.vo.S3ObjectFileVO;

import java.io.File;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
public interface IS3Operation {
    PutObjectResult putObject(String bucketName, String key, String file_path);

    S3Object getObject(String bucketName, String key);

    File convertFromS3Object(S3Object object, String fileName);

    S3ObjectFileVO getFileFromS3Object(S3Object object);
}
