package com.aws.bq.common.model.vo.base;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/9/2018
 */
@Data
public class MessageVO<T> {
    private Integer responseCode;
    private String responseMessage;
    private Integer pageIndex;
    private Integer pageTotal;
    private Integer totalCount;
    private List<T> data;
}
