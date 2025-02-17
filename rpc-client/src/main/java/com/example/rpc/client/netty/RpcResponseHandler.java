package com.example.rpc.client.netty;

import com.example.rpc.messages.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        log.info("Client receive response: {}", rpcResponse);
        // set the response to the promise
        RequestPromise requestPromise = RpcRequestContainer.getRequestPromise(rpcResponse.getRequestId());
        if (requestPromise != null) {
            requestPromise.setSuccess(rpcResponse);
        }
    }
}
