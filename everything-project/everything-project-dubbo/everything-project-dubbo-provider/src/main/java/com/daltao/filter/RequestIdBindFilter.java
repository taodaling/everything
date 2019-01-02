package com.daltao.filter;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Priority(0)
@PreMatching
@Provider
public class RequestIdBindFilter implements Filter {
    private static final String REQUEST_ID_KEY = "requestId";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            String requestId = invocation.getAttachment(REQUEST_ID_KEY);
            if (requestId == null) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put(REQUEST_ID_KEY, requestId);
            return invoker.invoke(invocation);
        } finally {
            MDC.remove(REQUEST_ID_KEY);
        }
    }
}
