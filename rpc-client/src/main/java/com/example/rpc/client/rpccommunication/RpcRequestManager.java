package com.example.rpc.client.rpccommunication;

import com.example.rpc.client.cache.ServiceProviderCache;
import com.example.rpc.client.cluster.LoadBalanceStrategy;
import com.example.rpc.client.cluster.StrategyProvider;
import com.example.rpc.client.netty.ChannelMapping;
import com.example.rpc.client.netty.RequestPromise;
import com.example.rpc.client.netty.RpcRequestContainer;
import com.example.rpc.client.netty.RpcResponseHandler;
import com.example.rpc.client.util.ServiceProvider;
import com.example.rpc.messages.RpcRequest;
import com.example.rpc.messages.RpcResponse;
import com.example.rpc.netty.codec.RpcRequestSerializer;
import com.example.rpc.netty.codec.RpcResponseDeserializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class RpcRequestManager {
    @Autowired
    private StrategyProvider strategyProvider;

    @Autowired
    private ServiceProviderCache serviceProviderCache;

    public RpcResponse sendRequest(RpcRequest request) {
        // todo: for debugging, delete later
        System.out.println("entering sendRequest method: " + request);

        // use service provider to send the request, so we need to find the service provider
        List<ServiceProvider> serviceProviders = serviceProviderCache.get(request.getClassName());
        if (serviceProviders == null || serviceProviders.isEmpty()) {
            log.error("No available service provider found for class {}", request.getClassName());
            return null;
        }

        // use the strategy provider to select a service provider
        LoadBalanceStrategy strategy = strategyProvider.getStrategy();
        ServiceProvider serviceProvider = strategy.select(serviceProviders);

        // todo: for debugging purposes, delete later
        System.out.println("At the end of sendRequest method, before requestByNetty method, serviceProvider = " + serviceProvider);

        return requestByNetty(serviceProvider, request);
    }

    private RpcResponse requestByNetty(ServiceProvider serviceProvider, RpcRequest request) {
        // todo: for debugging purposes, delete later
        System.out.println("Entering requestByNetty method...");

        Channel channel = null;
        if (!RpcRequestContainer.channelExist(serviceProvider.getServerIp(), serviceProvider.getRpcPort())) {
            // the channel does not exist, we need to create a new one
            NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("rpc-client"));
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("tcpFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("tcpFrameEncoder", new LengthFieldPrepender(4));
                            pipeline.addLast("rpcRequestEncoder", new RpcRequestSerializer());
                            pipeline.addLast("rpcResponseDecoder", new RpcResponseDeserializer());
                            // add a handler that recognizes the response
                            pipeline.addLast("rpcResponseHandler", new RpcResponseHandler());
                        }
                    });
            ChannelFuture future = null;

            try {

                // todo: for debugging purposes, delete later
                System.out.println("Connecting to the service provider: " + serviceProvider.getServerIp() + ":" + serviceProvider.getRpcPort());

                future = bootstrap.connect(serviceProvider.getServerIp(), serviceProvider.getRpcPort()).sync();
                if (future.isSuccess()) {
                    // todo: for debugging purposes, delete later
                    System.out.println("Connection successful");

                    channel = future.channel();
                    RpcRequestContainer.addChannelMapping(new ChannelMapping(serviceProvider.getServerIp(), serviceProvider.getRpcPort(), channel));
                }
            } catch (InterruptedException e) {
                log.error("Failed to connect to the service provider", e);
            }
        }

        // todo: for debugging purposes, delete later
        System.out.println("channel = " + channel);

//        if (channel == null) {
            channel = RpcRequestContainer.getChannelMapping(serviceProvider.getServerIp(), serviceProvider.getRpcPort()).getChannel();
//        }

        // start sending the request
        // todo: for debugging purposes, delete later
        System.out.println("Sending the request to the server: " + request.toString());
        System.out.println("serviceProvider = " + serviceProvider);

        RequestPromise requestPromise = new RequestPromise(channel.eventLoop());
        RpcRequestContainer.addRequestPromise(request.getRequestId(), requestPromise); // for later response matching
        ChannelFuture channelFuture = channel.writeAndFlush(request);

        // wait for the response
        try {
            return (RpcResponse) requestPromise.get();
        } catch (Exception e) {
            log.error("Failed to get the response", e);
        } finally {
            RpcRequestContainer.removeRequestPromise(request.getRequestId());
        }

        return RpcResponse.builder().build();
    }
}
