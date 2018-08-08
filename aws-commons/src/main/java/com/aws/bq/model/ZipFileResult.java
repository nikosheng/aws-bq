package com.aws.bq.model;

import lombok.Data;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
@Data
public class ZipFileResult {
    private boolean success;

    public ZipFileResult(boolean success) {
        this.success = success;
    }
}
