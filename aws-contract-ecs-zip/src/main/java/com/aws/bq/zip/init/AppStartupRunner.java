package com.aws.bq.zip.init;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.aws.bq.common.model.ZipFileResult;
import com.aws.bq.common.model.vo.ContractResponseVO;
import com.aws.bq.common.model.vo.S3ObjectFileVO;
import com.aws.bq.common.model.vo.base.MessageVO;
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
//        log.info("[AppStartupRunner] =========> Parameter: " + parameters);

        try {
            log.debug("[AppStartupRunner] =========> Start Main Login......");
            JSONObject jsonObject = JSONObject.parseObject(parameters);
            String jsonStr = jsonObject.toJSONString();
            log.info("[AppStartupRunner] =========> Parameter: " + jsonStr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);
            String response = restTemplate.postForObject(
                    ELB_CONTRACT_DNS + "/contract/search",
                    entity, String.class);
            log.info("[AppStartupRunner] =========> Response: " + response);
            MessageVO messageVO = JSONObject.parseObject(response, MessageVO.class);

            List<ContractResponseVO> contracts = Lists.transform((List<JSONObject>) messageVO.getData(),
                    new Function<JSONObject, ContractResponseVO>() {
                @Override
                public ContractResponseVO apply(@Nullable JSONObject jsonObject) {
                    return jsonObject.toJavaObject(ContractResponseVO.class);
                }
            });

            List<File> files = Lists.transform(contracts, new Function<ContractResponseVO, File>() {
                        @Override
                        public File apply(@Nullable ContractResponseVO contract) {
                            log.info("[AppStartupRunner] =========> " + contract.getS3Bucket() + " : " + contract.getS3Key());
                            S3Object object = S3Utils.getObject(contract.getS3Bucket(), contract.getS3Key());
                            S3ObjectFileVO vo = S3Utils.getFileFromS3Object(object);
                            return S3Utils.convertFromS3Object(object, vo.getFileName());
                        }
                    }
            );

            String generatedZipFile = String.format("bq-contracts-%d.%s", System.currentTimeMillis(), "zip");
            ZipFileResult result = Utils.zipFiles(files, generatedZipFile);
            String s3Key = ZIP_S3_PREFIX + generatedZipFile;
            if (result.isSuccess()) {
                PutObjectResult res = S3Utils.putObject(BUCKET_NAME, s3Key, generatedZipFile);
                URL url = S3Utils.getUrl(BUCKET_NAME, s3Key);
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

