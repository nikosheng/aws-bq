package com.aws.bq.controller;

import com.aws.bq.model.Contract;
import com.aws.bq.model.vo.ContractVO;
import com.aws.bq.service.IContractService;
import com.aws.bq.util.Utils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@Controller
@Slf4j
@RequestMapping("/contract")
public class ContractController {
    @Autowired
    private IContractService contractService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public void insert(@RequestBody @NonNull ContractVO contractVO) {
        log.info("Inserting an item to database......");
        Contract contract = new Contract();
        contract.setContractId(Utils.generateUUID());
        contract.setClientNum(contractVO.getContractNum());
        int num = contractService.insert(contract);
        log.info("Inserted " + num + " item(s)");
    }
}
