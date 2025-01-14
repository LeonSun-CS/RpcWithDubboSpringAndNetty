package com.example.rpc.netty.handler;

import com.example.rpc.messages.RpcRequest;
import com.example.rpc.messages.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        // set the response bean to be returned to the client
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        // get the service bean from the spring context
        try {
            Object bean = applicationContext.getBean(rpcRequest.getClassName());
            Object result = bean.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes()).invoke(bean, rpcRequest.getParameters());
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e.getMessage());
        } finally {
            channelHandlerContext.writeAndFlush(response);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RequestHandler.applicationContext = applicationContext;
    }
}
