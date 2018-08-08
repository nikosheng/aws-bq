package com.aws.bq.common.model.vo;

import lombok.Data;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@Data
public class S3ObjectFileVO {
    private String fileName;
    private String prefix;
    private String suffix;
}
