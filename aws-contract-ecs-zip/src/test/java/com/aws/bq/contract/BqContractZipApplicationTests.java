package com.aws.bq.contract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.util.Utils;
import com.aws.bq.contract.dao.IContractDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BqContractZipApplicationTests {
    @Autowired
    private IContractDAO contractDAO;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testFind() {
//        List<Contract> contracts = contractDAO.findAll();
        ContractRequestVO contract = new ContractRequestVO();
        contract.setContractNum("test");
        List<Contract> contracts = contractDAO.findByContract(contract);
        System.out.println(contracts.size());
    }

    @Test
    public void testAdd() {
        Contract contract = new Contract();
        contract.setContractId(Utils.generateUUID());
        contract.setContractNum("test");
        contract.setS3Bucket("testBucket");
        contract.setS3Key("testKey");
        contract.setCreateTime(new java.util.Date());
        contract.setUpdateTime(new java.util.Date());
        int num = contractDAO.insert(contract);
    }

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
}
