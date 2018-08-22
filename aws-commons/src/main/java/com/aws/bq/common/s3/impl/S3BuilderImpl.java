package com.aws.bq.common.s3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.aws.bq.common.model.vo.S3ObjectFileVO;
import com.aws.bq.common.s3.IS3Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/22/2018
 */
@Slf4j
public class S3BuilderImpl implements IS3Builder {
    @Override
    public PutObjectResult putObject(AmazonS3 amazonS3, String bucketName, String key, String file_path) {
        return amazonS3.putObject(
                new PutObjectRequest(bucketName, key, new File(file_path))
                        .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    @Override
    public File getObject(AmazonS3 amazonS3, String bucketName, String key) {
        S3Object object = amazonS3.getObject(bucketName, key);
        S3ObjectFileVO vo = getFileFromS3Object(object);
        return convertFromS3Object(object, vo.getFileName());
    }

    @Override
    public URL getUrl(AmazonS3 amazonS3, String bucket, String key) {
        return amazonS3.getUrl(bucket, key);
    }

    /**
     * 转换S3对象为File对象
     * @param object
     * @param fileName
     * @return
     */
    private File convertFromS3Object(S3Object object, String fileName) {
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
    private S3ObjectFileVO getFileFromS3Object(S3Object object) {
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
}
