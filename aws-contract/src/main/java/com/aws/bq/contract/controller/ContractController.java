package com.aws.bq.contract.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.ecs.model.*;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.ContractResponseVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.util.ECSUtils;
import com.aws.bq.common.util.ExcelUtils;
import com.aws.bq.common.util.Utils;
import com.aws.bq.contract.model.ContractExcel;
import com.aws.bq.contract.service.IContractService;
import com.aws.bq.contract.service.IPropertiesService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpStatus;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

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
    @Value("${amazon.ecs.cluster.name}")
    private String CLUSTER_NAME;
    @Value("${amazon.ecs.task.tag}")
    private String TASK_TAG;
    @Value("${amazon.ecs.task.container.name}")
    private String CONTAINER_NAME;
    @Value("${amazon.ecs.task.container.key.contract}")
    private String CONTAINER_ENV_KEY_CONTRACT;

    @Autowired
    private IContractService contractService;
    @Autowired
    private IPropertiesService propertiesService;

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
    public MessageVO search(@RequestBody ContractRequestVO contractRequestVO) {
        log.info("[ContractController] =========> Search item(s) in database......");

        // 分页设置
        Integer pageIndex = null == contractRequestVO.getPageIndex() ? DEFAULT_PAGE_INDEX : contractRequestVO.getPageIndex();
        Integer pageSize = null == contractRequestVO.getPageSize() ? DEFAULT_PAGE_SIZE : contractRequestVO.getPageSize();

        PageHelper.startPage(pageIndex, pageSize);
        List<Contract> contracts = contractService.findByContract(contractRequestVO);
        PageInfo<Contract> pageInfo = new PageInfo<>(contracts);
        List<ContractResponseVO> contractVOs = Lists.transform(contracts, new Function<Contract, ContractResponseVO>() {
            @Override
            public ContractResponseVO apply(@Nullable Contract contract) {
                ContractResponseVO vo = new ContractResponseVO();
                return vo.convert(contract);
            }
        });
        MessageVO messageVO = new MessageVO();
        messageVO.setResponseCode(HttpStatus.SC_OK);
        messageVO.setData(contractVOs);
        messageVO.setPageIndex(pageInfo.getPageNum());
        messageVO.setPageTotal(pageInfo.getPages());
        messageVO.setTotalCount((int) pageInfo.getTotal());
        messageVO.setResponseMessage("Success");
        return messageVO;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public MessageVO list(@RequestBody ContractRequestVO contractRequestVO) {
        log.info("[ContractController] =========> List all item(s) in database......");

        List<Contract> contracts = contractService.findByContract(contractRequestVO);
        MessageVO messageVO = new MessageVO();
        messageVO.setResponseCode(HttpStatus.SC_OK);
        messageVO.setData(contracts);
        messageVO.setResponseMessage("Success");
        return messageVO;
    }

    @RequestMapping(value = "/zip", produces = "application/json")
    @ResponseBody
    public MessageVO zip(@RequestBody ContractRequestVO contractRequestVO) {
        log.info("[ContractController] =========> Trigger zip ecs task ......");
        MessageVO messageVO = new MessageVO();

        String taskDef = propertiesService.getString("task_def", "contract-zip-taskref:18");
        RunTaskRequest request = new RunTaskRequest().withCluster(CLUSTER_NAME)
                .withTaskDefinition(taskDef)
                .withStartedBy(TASK_TAG)
                .withOverrides(
                        new TaskOverride().withContainerOverrides(
                                new ContainerOverride()
                                        .withName(CONTAINER_NAME)
                                        .withEnvironment(
                                                new KeyValuePair()
                                                        .withName(CONTAINER_ENV_KEY_CONTRACT)
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

    @RequestMapping(value = "/excel/export", produces = "application/json")
    @ResponseBody
    public MessageVO exportExcel(@RequestBody ContractRequestVO contractRequestVO, HttpServletResponse response) {
        MessageVO messageVO = new MessageVO();

        try {
            MessageVO resultVO = list(contractRequestVO);
            List<Contract> contracts = (List<Contract>) resultVO.getData();

            List<ContractExcel> excelVOs = Lists.transform(contracts, new Function<Contract, ContractExcel>() {
                @Override
                public ContractExcel apply(@Nullable Contract contract) {
                    ContractExcel vo = new ContractExcel();
                    return vo.convert(contract);
                }
            });

            ExcelUtils.exportExcel(excelVOs,
                    "aws-bq",
                    "contracts",
                    ContractExcel.class,
                    "合同文件清单_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"),
                    true,
                    response);

            messageVO.setTotalCount(excelVOs.size());
            messageVO.setResponseCode(HttpStatus.SC_OK);
            messageVO.setResponseMessage("Success");
        } catch (Exception e) {
            log.error("[ContractController] ==========> Exception: ", e);
        }
        return messageVO;
    }
}
