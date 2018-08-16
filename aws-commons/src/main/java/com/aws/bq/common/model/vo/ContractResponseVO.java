package com.aws.bq.common.model.vo;

import com.aws.bq.common.model.Contract;
import com.aws.bq.common.model.enumeration.CapitalEnum;
import com.aws.bq.common.model.enumeration.ContractStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.http.client.utils.DateUtils;

import java.util.Date;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/15/2018
 */
@Data
public class ContractResponseVO {
    /**
     * 合同ID
     */
    private String contractId;

    /**
     * 合同编号
     */
    private String contractNum;

    /**
     * 客户姓名
     */
    private String clientName;

    /**
     * 客户手机
     */
    private String clientMobile;

    /**
     * 客户编号
     */
    private String clientNum;

    /**
     * 资方
     */
    private String capital;

    /**
     * 合同状态
     */
    private String contractStatus;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 目录
     */
    private String directory;

    /**
     * 签署时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String signDate;

    /**
     * 身份证
     */
    private String identityCardNum;

    /**
     * Amazon S3桶名称
     */
    private String s3Bucket;

    /**
     * Amazon S3 键
     */
    private String s3Key;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 删除标志
     */
    private Integer del;

    /**
     * 转换Contract对象
     * @param contract
     * @return
     */
    public ContractResponseVO convert(Contract contract) {
        this.contractId = contract.getContractId();
        this.contractName = contract.getContractName();
        this.contractNum = contract.getContractNum();
        this.contractStatus = ContractStatusEnum.from(contract.getContractStatus()).getStatusName();
        this.capital = CapitalEnum.from(contract.getCapital()).getCapitalName();
        this.clientMobile = contract.getClientMobile();
        this.clientName = contract.getClientName();
        this.clientNum = contract.getClientNum();
        this.directory = contract.getDirectory();
        this.identityCardNum = contract.getIdentityCardNum();
        this.s3Bucket = contract.getS3Bucket();
        this.s3Key = contract.getS3Key();
        this.signDate = DateUtils.formatDate(contract.getSignDate(), "yyyy-MM-dd HH:mm:ss");
        this.createTime = contract.getCreateTime();
        this.updateTime = contract.getUpdateTime();
        this.operator = contract.getOperator();
        this.del = contract.getDel();
        return this;
    }
}
