package com.aws.bq.common.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@Data
public class ContractRequestVO {
    private String contractId;
    private String contractNum;
    private String clientName;
    private String clientMobile;
    private String clientNum;
    private String capital;
    private Integer contractStatus;
    private String contractName;
    private String directory;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date signDateStart;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date signDateEnd;
    private String identityCardNum;
    private String s3Bucket;
    private String s3Key;
    private Date createTime;
    private Date updateTime;
    private String operator;
    private Integer del;
    private Integer pageIndex;
    private Integer pageSize;
}
