package com.daltao.service;

import com.daltao.api.DemoService;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Service
@Path("/demo")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DemoServiceImpl implements DemoService {
    @Path("/say")
    @POST
    public Map<Object, Object> sayHello(Map<Object, Object> name) {
        Map<Object, Object> result = new HashMap<>();
        result.put("greeting", "Hello, world" + name.get("name"));
        return result;
    }
}