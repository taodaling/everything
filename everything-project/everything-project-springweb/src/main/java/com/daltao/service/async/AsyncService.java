package com.daltao.service.async;

import com.google.common.util.concurrent.Futures;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Future;

@Service
@EnableAsync
public class AsyncService {
    @Async
    public Future<Date> asyncInvoke() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Futures.immediateFuture(new Date());
    }
}

