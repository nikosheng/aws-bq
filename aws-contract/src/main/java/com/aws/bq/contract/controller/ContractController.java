package com.aws.bq.contract.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.ecs.model.*;
import com.amazonaws.services.sqs.model.Message;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.util.ECSUtils;
import com.aws.bq.common.util.SQSUtils;
import com.aws.bq.common.util.Utils;
import com.aws.bq.contract.service.IContractService;
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
    @Value("${amazon.ecs.cluster.name}")
    private String CLUSTER_NAME;
    @Value("${amazon.ecs.task.definition}")
    private String TASK_DEFINITION;
    @Value("${amazon.ecs.task.tag}")
    private String TASK_TAG;
    @Value("${amazon.ecs.task.container.name}")
    private String CONTAINER_NAME;
    @Value("${amazon.ecs.task.container.key.contract}")
    private String CONTAINER_ENV_KEY_CONTRACT;

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
    public MessageVO search(@RequestBody ContractRequestVO contractVO) {
        log.info("[ContractController] =========> Search item(s) in database......");

        // 分页设置
        Integer pageIndex = OptionalInt.of(contractVO.getPageIndex()).orElse(DEFAULT_PAGE_INDEX);
        Integer pageSize = OptionalInt.of(contractVO.getPageSize()).orElse(DEFAULT_PAGE_SIZE);

        PageHelper.startPage(pageIndex, pageSize);
        List<Contract> contracts = contractService.findByContract(contractVO);
        PageInfo<Contract> pageInfo = new PageInfo<>(contracts);
        MessageVO messageVO = new MessageVO();
        messageVO.setResponseCode(HttpStatus.SC_OK);
        messageVO.setData(contracts);
        messageVO.setPageIndex(pageInfo.getPageNum());
        messageVO.setPageTotal(pageInfo.getPages());
        messageVO.setTotalCount((int) pageInfo.getTotal());
        messageVO.setResponseMessage("Success");
        return messageVO;
    }

    @RequestMapping(value = "/zip", produces = "application/json")
    @ResponseBody
    public MessageVO zip(@RequestBody ContractRequestVO contractRequestVO) {
        log.info("[ContractController] =========> Trigger zip ecs task ......");
        MessageVO messageVO = new MessageVO();

        RunTaskRequest request = new RunTaskRequest().withCluster(CLUSTER_NAME)
                .withTaskDefinition(TASK_DEFINITION)
                .withStartedBy(TASK_TAG)
                .withOverrides(
                        new TaskOverride().withContainerOverrides(
                                new ContainerOverride()
                                        .withName(CONTAINER_NAME)
                                        .withEnvironment(
                                                new KeyValuePair()
                                                        .withName(CONTAINER_ENV_KEY_CONTRACT)
//                                                        .withValue("{\"contractNum\": \"V10021\",\"signDateStart\": \"2018-08-01 10:50:19\",\"signDateEnd\": \"2018-08-10 10:50:19\",\"del\": 0,\"pageIndex\": 1,\"pageSize\": 10}"))));
                                                        .withValue(JSONObject.toJSONString(contractRequestVO)))));
        RunTaskResult response = ECSUtils.runTask(request);
        List<Task> tasks = response.getTasks();
        if (null == tasks || tasks.size() == 0) {
            List<Failure> failures = response.getFailures();
            messageVO.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            messageVO.setData(failures);
            messageVO.setResponseMessage("Fail");
        }

        messageVO.setResponseCode(HttpStatus.SC_OK);
        messageVO.setData(tasks);
        messageVO.setResponseMessage("Success");
        return messageVO;
    }
}
