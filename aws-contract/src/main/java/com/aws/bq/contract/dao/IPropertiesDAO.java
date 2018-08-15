package com.aws.bq.contract.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/13/2018
 */
public interface IPropertiesDAO {
    String get(@Param("propertyKey") String key);
}
