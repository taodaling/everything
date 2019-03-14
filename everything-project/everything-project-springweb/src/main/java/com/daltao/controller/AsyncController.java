package com.daltao.controller;

import com.daltao.service.async.AsyncService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/async")
public class AsyncController {
    @Resource
    AsyncService asyncService;

    @RequestMapping("/invoke")
    public String invoke() throws ExecutionException, InterruptedException {
        return asyncService.asyncInvoke().get().toString();
    }
}
