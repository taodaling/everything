package com.daltao.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.daltao.api.TimeService;
import org.springframework.stereotype.Component;

@Service(version = "1.0.0")
public class TimeServiceImpl implements TimeService {
    @Override
    public long getElapsedMilliSecondsSinceEpoch() {
        return System.currentTimeMillis();
    }
}
