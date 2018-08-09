package com.aws.bq.contract.controller;

import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.contract.service.IContractService;
import com.aws.bq.common.util.Utils;
import com.aws.bq.common.model.Contract;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.OptionalInt;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@Controller
@Slf4j
@RequestMapping("/contract")
public class ContractController {
    public static final int DEFAULT_PAGE_INDEX = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private IContractService contractService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public void insert(@RequestBody @NonNull ContractRequestVO contractVO) {
        log.info("Inserting an item to database......");
        Contract contract = new Contract();
        contract.setContractId(Utils.generateUUID());
        contract.setClientNum(contractVO.getContractNum());
        int num = contractService.insert(contract);
        log.info("Inserted " + num + " item(s)");
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public MessageVO<Contract> search(@RequestBody ContractRequestVO contractVO) {
        log.info("Search item(s) in database......");

        // 分页设置
        Integer pageIndex = OptionalInt.of(contractVO.getPageIndex()).orElse(DEFAULT_PAGE_INDEX);
        Integer pageSize = OptionalInt.of(contractVO.getPageSize()).orElse(DEFAULT_PAGE_SIZE);

        PageHelper.startPage(pageIndex, pageSize);
        List<Contract> contracts = contractService.findByContract(contractVO);
        PageInfo<Contract> pageInfo = new PageInfo<>(contracts);
        MessageVO<Contract> messageVO = new MessageVO<>();
        messageVO.setResponseCode(HttpStatus.SC_OK);
        messageVO.setData(contracts);
        messageVO.setPageIndex(pageInfo.getPageNum());
        messageVO.setPageTotal(pageInfo.getPages());
        messageVO.setTotalCount((int) pageInfo.getTotal());
        messageVO.setResponseMessage("Success");
        return messageVO;
    }
}
