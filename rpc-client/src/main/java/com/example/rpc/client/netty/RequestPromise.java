package com.example.rpc.client.netty;

import com.example.rpc.messages.RpcResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;

public class RequestPromise extends DefaultPromise<RpcResponse> {
    public RequestPromise(EventExecutor executor) {
        super(executor);
    }
}
