package com.aws.bq.contract.controller;

import com.amazonaws.services.sqs.model.Message;
import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.util.SQSUtils;
import com.aws.bq.contract.service.IContractService;
import com.aws.bq.common.util.Utils;
import com.aws.bq.common.model.Contract;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@Controller
@Slf4j
@RequestMapping("/contract")
public class ContractController {
    @Value("${constants.page-index:1}")
    private int DEFAULT_PAGE_INDEX;
    @Value("${constants.page-size:10}")
    private int DEFAULT_PAGE_SIZE;
    @Value("${amazon.sqs.queue.url}")
    private String MESSAGE_QUEUE_URL;

    @Autowired
    private IContractService contractService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public void insert(@RequestBody @NonNull ContractRequestVO contractVO) {
        log.info("[ContractController] =========> Inserting an item to database......");
        Contract contract = new Contract();
        contract.setContractId(Utils.generateUUID());
        contract.setClientNum(contractVO.getContractNum());
        int num = contractService.insert(contract);
        log.info("Inserted " + num + " item(s)");
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public MessageVO<Contract> search(@RequestBody ContractRequestVO contractVO) {
        log.info("[ContractController] =========> Search item(s) in database......");

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

    @RequestMapping(value = "/message", produces = "application/json")
    @ResponseBody
    public MessageVO<Message> message() {
        log.info("[ContractController] =========> Receive item(s) in SQS......");

        while (true) {
            List<Message> messages = SQSUtils.receiveMessage(MESSAGE_QUEUE_URL);
            if (null != messages && messages.size() > 0) {
                MessageVO<Message> messageVO = new MessageVO<>();
                messageVO.setResponseCode(HttpStatus.SC_OK);
                messageVO.setData(messages);
                messageVO.setTotalCount(messages.size());
                messageVO.setResponseMessage("Success");

                SQSUtils.deleteMessage(MESSAGE_QUEUE_URL, messages.get(0).getReceiptHandle());
                return messageVO;
            }
            try {
                Thread.sleep(TimeUnit.SECONDS.toSeconds(5));
            } catch (InterruptedException e) {
                log.error("[ContractController] =========> Exception: ", e);
            }
        }
    }
}
