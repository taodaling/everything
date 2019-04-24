package com.daltao.simple;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HTTPTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.setProperty("java.net.useSystemProxies", "true");
        AsyncHttpClient client = new AsyncHttpClient();
        ListenableFuture<Response> future = client.prepareGet("https://www.google.com").execute();
        System.out.println(future.get());
        client.close();
    }

}
