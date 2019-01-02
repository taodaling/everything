package com.daltao.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Activate(group = Constants.PROVIDER, order = 1)
public class LogFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            log.info("Invoker {} invoke {}", invoker, invocation);
            Result result = invoker.invoke(invocation);
            log.info("Response {}", result);
            return result;
        } catch (Throwable e) {
            log.error("Uncaught exception", e);
            return new RpcResult(e);
        }
    }
}
