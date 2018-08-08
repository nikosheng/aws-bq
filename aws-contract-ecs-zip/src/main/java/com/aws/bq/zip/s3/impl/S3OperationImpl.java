package com.aws.bq.zip.s3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.aws.bq.common.model.vo.S3ObjectFileVO;
import com.aws.bq.zip.s3.IS3Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
@Service
@Slf4j
public class S3OperationImpl implements IS3Operation {

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public PutObjectResult putObject(String bucketName, String key, String file_path) {
        return amazonS3.putObject(
                new PutObjectRequest(bucketName, key, new File(file_path))
                        .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    @Override
    public S3Object getObject(String bucketName, String key) {
        return amazonS3.getObject(bucketName, key);
    }

    @Override
    public File convertFromS3Object(S3Object object, String fileName) {
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

    @Override
    public S3ObjectFileVO getFileFromS3Object(S3Object object) {
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
