package com.aws.bq.zip.init;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.aws.bq.zip.ecs.IECSOperation;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.ZipFileResult;
import com.aws.bq.common.model.vo.S3ObjectFileVO;
import com.aws.bq.zip.s3.IS3Operation;
import com.aws.bq.contract.service.IContractService;
import com.aws.bq.common.util.Utils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/3/2018
 */
@Component
@Slf4j
public class AppStartupRunner implements CommandLineRunner {
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private IS3Operation s3ops;
    @Autowired
    private IContractService contractService;
    @Autowired
    private IECSOperation ecsops;

    @Value("${amazon.s3.bucket}")
    private String BUCKET_NAME;
    @Value("${amazon.s3.zipPrefix}")
    private String ZIP_S3_PREFIX;
    @Value("${amazon.ecs.cluster.name}")
    private String ECS_CLUSTER_NAME;
    @Value("${amazon.ecs.task.tag}")
    private String ECS_TASK_BQ_TAG;

    @Override
    public void run(String... args) throws Exception {
        log.debug("[AppStartupRunner] =========> The app is running......");

        try {
            // 1. Search contracts with parameters in RDS
            List<Contract> contracts = contractService.findAll();

            // 2. Retrieve the S3 objects url
            List<File> files = Lists.transform(contracts, new Function<Contract, File>() {
                        @Override
                        public File apply(@Nullable Contract contract) {
                            S3Object object = s3ops.getObject(contract.getS3Bucket(), contract.getS3Key());
                            log.debug("[AppStartupRunner] =========> Get S3 Object [" + object.getKey() + "] ..........");
                            S3ObjectFileVO vo = s3ops.getFileFromS3Object(object);
                            return s3ops.convertFromS3Object(object, vo.getFileName());
                        }
                    }
            );

            String generatedZipFile = String.format("bq-contracts-%d.%s", System.currentTimeMillis(), "zip");
            ZipFileResult result = Utils.zipFiles(files, generatedZipFile);
            String s3Key = ZIP_S3_PREFIX + generatedZipFile;
            if (result.isSuccess()) {
                PutObjectResult res = s3ops.putObject(BUCKET_NAME, s3Key, generatedZipFile);
                URL url = amazonS3.getUrl(BUCKET_NAME, s3Key);
                log.debug("[AppStartupRunner] =========> Path: " + url.toString());
            } else {
                log.error("[AppStartupRunner] =========> Unable to upload zip file to S3: ");
            }
        } catch (Exception e) {
            log.error("[AppStartupRunner] =========> Exception:", e);
        } finally {
//            ecsops.stopAllTask(ECS_CLUSTER_NAME, ECS_TASK_BQ_TAG);
        }
    }
}

