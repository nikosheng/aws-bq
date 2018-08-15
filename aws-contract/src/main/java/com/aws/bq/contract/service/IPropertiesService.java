package com.aws.bq.contract.service;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/13/2018
 */
public interface IPropertiesService {
    String getString(String key);

    String getString(String key, String defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    double getDouble(String key, double defaultValue);
}
