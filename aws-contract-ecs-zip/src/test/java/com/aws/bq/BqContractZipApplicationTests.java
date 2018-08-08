package com.aws.bq;

import com.aws.bq.dao.IContractDAO;
import com.aws.bq.model.Contract;
import com.aws.bq.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BqContractZipApplicationTests {
    @Autowired
    private IContractDAO contractDAO;

    @Test
    public void testFind() {
//        List<Contract> contracts = contractDAO.findAll();
        Contract contract = new Contract();
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
}
