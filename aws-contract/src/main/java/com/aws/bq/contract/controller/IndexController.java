package com.aws.bq.contract.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/9/2018
 */
@Controller
@Slf4j
public class IndexController {

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        log.info("health check...");
        return "health check...";
    }
}
