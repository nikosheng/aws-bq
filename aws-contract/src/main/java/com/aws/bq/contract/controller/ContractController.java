package com.aws.bq.contract.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.ecs.model.*;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.enumeration.ContractStatusEnum;
import com.aws.bq.common.model.vo.ContractRequestVO;
import com.aws.bq.common.model.vo.ContractResponseVO;
import com.aws.bq.common.model.vo.base.MessageVO;
import com.aws.bq.common.util.ECSUtils;
import com.aws.bq.common.util.ExcelUtils;
import com.aws.bq.common.util.Utils;
import com.aws.bq.contract.service.IContractService;
import com.aws.bq.contract.service.IPropertiesService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.DateUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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
    @Value("${constants.page-index:1}")
    private int DEFAULT_PAGE_INDEX;
    @Value("${constants.page-size:10}")
    private int DEFAULT_PAGE_SIZE;
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
        Integer pageSize = null == contractRequestVO.getPageSize() ? DEFAULT_PAGE_SIZE : contractRequestVO.getPageIndex();

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

    @RequestMapping(value = "/zip", produces = "application/json")
    @ResponseBody
    public MessageVO zip(@RequestBody ContractRequestVO contractRequestVO) {
        log.info("[ContractController] =========> Trigger zip ecs task ......");
        MessageVO messageVO = new MessageVO();

        String taskDef = propertiesService.getString("task_def", "contract-zip-taskref:1");
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
        int totalNum = 0;

        try {
            MessageVO resultVO = search(contractRequestVO);
            List<ContractResponseVO> contracts = (List<ContractResponseVO>) resultVO.getData();

            ExcelUtils.ExcelVo vo = new ExcelUtils.ExcelVo();
            vo.setFileName("合同文件清单_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
            vo.addContentRow("合同号;客户姓名;客户手机号;客户编号;资方;合同状态;文件名;目录名;合同签署时间;身份证号".split(";"));
            if (!CollectionUtils.isEmpty(contracts)) {
                for (ContractResponseVO contract : contracts) {
                    vo.addContentRow(
                            contract.getContractNum(),
                            contract.getClientName(),
                            contract.getClientMobile(),
                            contract.getClientNum(),
                            contract.getCapital(),
                            contract.getContractStatus(),
                            contract.getContractName(),
                            contract.getDirectory(),
                            DateUtils.formatDate(contract.getSignDate()),
                            contract.getIdentityCardNum());
                    totalNum++;
                }
            }

            ExcelUtils.exportExcel(vo, response);
            messageVO.setTotalCount(totalNum);
            messageVO.setResponseCode(HttpStatus.SC_OK);
            messageVO.setResponseMessage("Success");
        } catch (Exception e) {
            log.error("[ContractController] ==========> Exception: ", e);
        }
        return messageVO;
    }
}
