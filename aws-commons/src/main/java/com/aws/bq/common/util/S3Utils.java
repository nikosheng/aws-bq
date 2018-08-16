package com.aws.bq.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.aws.bq.common.model.vo.S3ObjectFileVO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @Description: S3 工具类
 * @author: jiasfeng
 * @Date: 8/10/2018
 */
@Slf4j
public class S3Utils {
    /**
     * 上传S3对象
     * @param amazonS3
     * @param bucketName
     * @param key
     * @param file_path
     * @return
     */
    public static PutObjectResult putObject(AmazonS3 amazonS3, String bucketName, String key, String file_path) {
        return amazonS3.putObject(
                new PutObjectRequest(bucketName, key, new File(file_path))
                        .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    /**
     * 获取S3对象
     * @param amazonS3
     * @param bucketName
     * @param key
     * @return
     */
    public static S3Object getObject(AmazonS3 amazonS3, String bucketName, String key) {
        return amazonS3.getObject(bucketName, key);
    }

    /**
     * 转换S3对象为File对象
     * @param object
     * @param fileName
     * @return
     */
    public static File convertFromS3Object(S3Object object, String fileName) {
        File tmp = null;
        InputStream in = object.getObjectContent();
        try {
            tmp = new File(com.google.common.io.Files.createTempDir(), fileName);
            log.info("[AppStartupRunner] =========> Get Temp zip file [" + tmp.getAbsolutePath() + "] ..........");
            Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("[AppStartupRunner] =========> Exception: ", e);
        }
        return tmp;
    }

    /**
     * 获取S3ObjectFileVO
     * @param object
     * @return
     */
    public static S3ObjectFileVO getFileFromS3Object(S3Object object) {
        String key = object.getKey();
        S3ObjectFileVO vo = new S3ObjectFileVO();
        String fileName = key.substring(key.lastIndexOf("/") + 1);
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        vo.setFileName(fileName);
        vo.setPrefix(prefix);
        vo.setSuffix(suffix);
        return vo;
    }

    /**
     * 获取S3对象URL
     * @param amazonS3
     * @param bucket
     * @param key
     * @return
     */
    public static URL getUrl(AmazonS3 amazonS3, String bucket, String key) {
        return amazonS3.getUrl(bucket, key);
    }
}
