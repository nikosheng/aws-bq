package com.aws.bq.contract.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.enumeration.ContractStatusEnum;
import lombok.Data;
import org.apache.http.client.utils.DateUtils;

import java.util.Date;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/15/2018
 */
@Data
public class ContractExcel {

    /**
     * 合同编号
     */
    @Excel(name = "合同编号", orderNum = "0")
    private String contractNum;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名", orderNum = "1")
    private String clientName;

    /**
     * 客户手机
     */
    @Excel(name = "客户手机", orderNum = "2")
    private String clientMobile;

    /**
     * 客户编号
     */
    @Excel(name = "客户编号", orderNum = "3")
    private String clientNum;

    /**
     * 资方
     */
    @Excel(name = "资方", orderNum = "4")
    private String capital;

    /**
     * 合同状态
     */
    @Excel(name = "合同状态", orderNum = "5")
    private String contractStatus;

    /**
     * 合同名称
     */
    @Excel(name = "合同名称", orderNum = "6")
    private String contractName;

    /**
     * 目录
     */
    @Excel(name = "目录", orderNum = "7")
    private String directory;

    /**
     * 签署时间
     */
    @Excel(name = "签署时间", orderNum = "8")
    private String signDate;

    /**
     * 身份证
     */
    @Excel(name = "身份证", orderNum = "9")
    private String identityCardNum;

    /**
     * 转换Contract对象
     * @param contract
     * @return
     */
    public ContractExcel convert(Contract contract) {
        this.contractName = contract.getContractName();
        this.contractNum = contract.getContractNum();
        this.contractStatus = ContractStatusEnum.from(contract.getContractStatus()).getStatusName();
        this.capital = contract.getCapital();
        this.clientMobile = contract.getClientMobile();
        this.clientName = contract.getClientName();
        this.clientNum = contract.getClientNum();
        this.directory = contract.getDirectory();
        this.identityCardNum = contract.getIdentityCardNum();
        this.signDate = DateUtils.formatDate(contract.getSignDate(), "yyyy-MM-dd HH:mm:ss");
        return this;
    }
}


