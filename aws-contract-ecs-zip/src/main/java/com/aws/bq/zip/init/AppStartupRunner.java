package com.aws.bq.zip.init;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.ZipFileResult;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.s3.S3Client;
import com.aws.bq.common.util.ECSUtils;
import com.aws.bq.common.util.S3Utils;
import com.aws.bq.common.util.SNSUtils;
import com.aws.bq.common.util.Utils;
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
    private RestTemplate restTemplate;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private TransferManager manager;

    @Value("${amazon.s3.bucket}")
    private String BUCKET_NAME;
    @Value("${amazon.s3.zipPrefix}")
    private String ZIP_S3_PREFIX;
    @Value("${amazon.ecs.cluster.name}")
    private String ECS_CLUSTER_NAME;
    @Value("${amazon.ecs.task.tag}")
    private String ECS_TASK_BQ_TAG;
    @Value("${amazon.sns.topic.arn}")
    private String SNS_TOPIC_ARN;
    @Value("${amazon.elb.contract}")
    private String ELB_CONTRACT_DNS;

    /** Environment variables */
    private String parameters;

    @Override
    public void run(String... args) throws Exception {
        log.debug("[AppStartupRunner] =========> The app is running......");

        S3Client s3Client = S3Utils.getS3Client();

        try {
            JSONObject jsonObject = JSONObject.parseObject(parameters);
            String jsonStr = jsonObject.toJSONString();
            log.info("[AppStartupRunner] =========> Parameter: " + jsonStr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);
            String response = restTemplate.postForObject(
                    ELB_CONTRACT_DNS + "/contract/list",
                    entity, String.class);
            log.debug("[AppStartupRunner] =========> Response: " + response);
            MessageVO messageVO = JSONObject.parseObject(response, MessageVO.class);

            List<Contract> contracts = Lists.transform((List<JSONObject>) messageVO.getData(),
                    new Function<JSONObject, Contract>() {
                @Override
                public Contract apply(@Nullable JSONObject jsonObject) {
                    return jsonObject.toJavaObject(Contract.class);
                }
            });

            List<File> files = s3Client.downloadByTransferMgr(manager, contracts);
            String generatedZipFile = String.format("bq-contracts-%d.%s", System.currentTimeMillis(), "zip");
            ZipFileResult result = Utils.zipFiles(files, generatedZipFile);
            String s3Key = ZIP_S3_PREFIX + generatedZipFile;
            if (result.isSuccess()) {
                UploadResult res = s3Client.uploadByTransferMgr(manager, BUCKET_NAME, s3Key, new File(generatedZipFile));
                URL url = s3Client.getURL(manager, BUCKET_NAME, s3Key);
                log.debug("[AppStartupRunner] =========> Path: " + url.toString());
                // 发送压缩后的文件下载链接到SNS
                log.debug("[AppStartupRunner] =========> Send Message to SNS... Topic: " + SNS_TOPIC_ARN);
                SNSUtils.sendMessage(SNS_TOPIC_ARN, url.toString());
            } else {
                log.error("[AppStartupRunner] =========> Unable to upload zip file to S3: ");
            }
        } catch (Exception e) {
            log.error("[AppStartupRunner] =========> Exception:", e);
        } finally {
//            ECSUtils.stopAllTask(ECS_CLUSTER_NAME, ECS_TASK_BQ_TAG);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        log.debug("[AppStartupRunner] =========> Set environment variables......");
        this.parameters = environment.getProperty("contract-env");
    }
}

