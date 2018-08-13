package com.aws.bq.contract.service.impl;

import com.aws.bq.contract.dao.IPropertiesDAO;
import com.aws.bq.contract.service.IPropertiesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/13/2018
 */
@Service
@Slf4j
public class PropertiesServiceImpl implements IPropertiesService {
    @Autowired
    private IPropertiesDAO propertiesDAO;

    @Override
    public String getString(String key) {
        return (String) propertiesDAO.get(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String value = (String) propertiesDAO.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = (Boolean) propertiesDAO.get(key);
        if (null == value) {
            value = defaultValue;
        }
        return value;
    }

    @Override
    public double getInteger(String key, int defaultValue) {
        return NumberUtils.toInt(getString(key), defaultValue);
    }

    @Override
    public double getLong(String key, long defaultValue) {
        return NumberUtils.toLong(getString(key), defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return NumberUtils.toDouble(getString(key), defaultValue);
    }
}
