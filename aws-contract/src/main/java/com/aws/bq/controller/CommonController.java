package com.aws.bq.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/6/2018
 */
@RestController
public class CommonController {
    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }
}
