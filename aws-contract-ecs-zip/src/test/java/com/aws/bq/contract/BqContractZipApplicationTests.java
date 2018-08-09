package com.aws.bq.contract;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.vo.base.MessageVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BqContractZipApplicationTests {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testRestTemplate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        String input = "{\"contractId\":null,\"contractNum\":null,\"clientMobile\":null,\"clientNum\":null,\"capital\":null,\"contractStatus\":null,\"contractName\":null,\"directory\":null,\"signDateStart\":\"2018-08-01 10:50:19\",\"signDateEnd\":\"2018-08-10 10:50:19\",\"identityCardNum\":null,\"s3Bucket\":null,\"s3Key\":null,\"operator\":null,\"del\":0,\"pageIndex\":1,\"pageSize\":10}";

        HttpEntity<String> entity = new HttpEntity<>(input, headers);
        String response = restTemplate.postForObject(
                "http://aws-contract-lb-1813490861.cn-northwest-1.elb.amazonaws.com.cn:8088/contract/search",
                entity, String.class);
        MessageVO<Contract> vo = JSONObject.parseObject(response, MessageVO.class);
        System.out.println();
    }

    @Test
    public void testECS() {
        AmazonECS client = AmazonECSClientBuilder.standard().build();
        RunTaskRequest request = new RunTaskRequest().withCluster("default")
                .withTaskDefinition("contract-taskdef:7")
                .withStartedBy("bq")
                .withOverrides(
                        new TaskOverride().withContainerOverrides(
                                new ContainerOverride()
                                        .withEnvironment(
                                                new KeyValuePair()
                                                        .withName("contract-env")
                                                        .withValue("{\"contractNum\": \"V10021\",\"signDateStart\": \"2018-08-01 10:50:19\",\"signDateEnd\": \"2018-08-10 10:50:19\",\"del\": 0,\"pageIndex\": 1,\"pageSize\": 10}"))));
        RunTaskResult response = client.runTask(request);
    }
}
