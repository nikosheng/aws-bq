package com.aws.bq.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 描述:contract表的实体类
 * @version
 * @author:  jiasfeng
 * @创建时间: 2018-08-13
 */
@Data
public class Contract {
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
    private Integer contractStatus;

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
    private Date signDate;

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
}
