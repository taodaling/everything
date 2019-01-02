package com.daltao.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.daltao.api.TimeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/time")
@ResponseBody
public class TimeController {
    @Reference(interfaceClass = TimeService.class)
    private TimeService timeService;

    @RequestMapping("/elapsedMilliSecondsSinceEpoch")
    public Long elapsedMilliSecondsSinceEpoch() {
        return timeService.getElapsedMilliSecondsSinceEpoch();
    }
}
