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
        String value = propertiesDAO.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = propertiesDAO.get(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(propertiesDAO.get(key));
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return NumberUtils.toInt(getString(key), defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return NumberUtils.toLong(getString(key), defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return NumberUtils.toDouble(getString(key), defaultValue);
    }
}
