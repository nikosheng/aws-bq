package com.aws.bq.common.util;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/9/2018
 */
public class HttpUtils {
    public static <T> T postForObject(String url, Class<T> clazz, String ...args) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return null;
    }
}
