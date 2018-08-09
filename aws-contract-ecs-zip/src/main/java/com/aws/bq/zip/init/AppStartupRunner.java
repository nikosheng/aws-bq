package com.aws.bq.zip.init;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.ZipFileResult;
import com.aws.bq.common.model.vo.S3ObjectFileVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.util.Utils;
import com.aws.bq.zip.ecs.IECSOperation;
import com.aws.bq.zip.s3.IS3Operation;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
public class AppStartupRunner implements CommandLineRunner, EnvironmentAware {
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private IS3Operation s3ops;
    @Autowired
    private IECSOperation ecsops;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${amazon.s3.bucket}")
    private String BUCKET_NAME;
    @Value("${amazon.s3.zipPrefix}")
    private String ZIP_S3_PREFIX;
    @Value("${amazon.ecs.cluster.name}")
    private String ECS_CLUSTER_NAME;
    @Value("${amazon.ecs.task.tag}")
    private String ECS_TASK_BQ_TAG;
    @Value("${bq.contract-platform-dns}")
    private String BQ_CONTRACT_PLATFORM_DNS;

    private String parameters;

    @Override
    public void run(String... args) throws Exception {
        log.debug("[AppStartupRunner] =========> The app is running......");
        log.info("[AppStartupRunner] =========> Parameter: " + parameters);

        try {
            // 1. Search contracts with parameters in RDS
            JSONObject jsonObject = JSONObject.parseObject(parameters);
            String jsonStr = jsonObject.toJSONString();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);
            String response = restTemplate.postForObject(
                    BQ_CONTRACT_PLATFORM_DNS + "/contract/search",
                    entity, String.class);
            MessageVO<JSONObject> messageVO = JSONObject.parseObject(response, MessageVO.class);

            // 2. Retrieve the S3 objects url
            List<Contract> contracts = Lists.transform(messageVO.getData(), new Function<JSONObject, Contract>() {
                @Override
                public Contract apply(@Nullable JSONObject jsonObject) {
                    return jsonObject.toJavaObject(Contract.class);
                }
            });

            List<File> files = Lists.transform(contracts, new Function<Contract, File>() {
                        @Override
                        public File apply(@Nullable Contract contract) {
                            log.info("[AppStartupRunner] =========> " + contract.getS3Bucket() + " : " + contract.getS3Key());
                            S3Object object = s3ops.getObject(contract.getS3Bucket(), contract.getS3Key());
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
            ecsops.stopAllTask(ECS_CLUSTER_NAME, ECS_TASK_BQ_TAG);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.parameters = environment.getProperty("contract-env");
    }
}

