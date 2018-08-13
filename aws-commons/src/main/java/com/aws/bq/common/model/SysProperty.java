package com.aws.bq.common.model;

import java.util.Date;

/**
 * 描述:sys_property表的实体类
 * @version
 * @author:  jiasfeng
 * @创建时间: 2018-08-13
 */
public class SysProperty {
    /**
     * 属性ID
     */
    private String propertyId;

    /**
     * 属性键
     */
    private String propertyKey;

    /**
     * 属性值
     */
    private String propertyValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 删除标志
     */
    private Byte del;
}