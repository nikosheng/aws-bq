package com.aws.bq.common.model.enumeration;

import lombok.Getter;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/13/2018
 */
public enum ContractStatusEnum {
    NULL(0, "无状态"),
    INIT(1, "合同初始化"),
    INPROGRESS(2, "合同进行中"),
    DEAL(3, "已结清");

    @Getter
    private Integer status;
    @Getter
    private String statusName;

    ContractStatusEnum(Integer status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static ContractStatusEnum from(Integer status) {
        if (null != status) {
            for (ContractStatusEnum item : ContractStatusEnum.values()) {
                if (item.getStatus().equals(status)) {
                    return item;
                }
            }
        }
        return NULL;
    }
}
