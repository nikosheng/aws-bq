package com.aws.bq.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
@Data
public class Contract {
    private String contractId;
    private String contractNum;
    private String clientMobile;
    private String clientNum;
    private String capital;
    private Integer contractStatus;
    private String contractName;
    private String directory;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date signDate;
    private String identityCardNum;
    private String s3Bucket;
    private String s3Key;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    private String operator;
    private Integer del;
}
