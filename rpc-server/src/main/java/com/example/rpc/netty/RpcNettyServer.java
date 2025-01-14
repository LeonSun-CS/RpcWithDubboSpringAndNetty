package com.example.rpc.netty;

import com.example.rpc.netty.codec.RpcRequestDeserializer;
import com.example.rpc.netty.codec.RpcResponseSerializer;
import com.example.rpc.netty.handler.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RpcNettyServer implements RpcServer {
    /**
     * Start the server. As a netty server, it should bind to a port and listen to the incoming requests.
     * The server will be started by the RpcServerRunner class, after the service registration.
     * TCP frame: LengthFieldBasedFrame
     * Serialization: Protostuff
     *
     */
    @Override
    public void start() {
        // we need two event loop groups, one for the boss thread and the other for the worker thread
        // the boss thread responsible for accepting the incoming connections only need one thread
        NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        // the worker thread responsible for processing the requests need more threads
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        // after we get the processed data from the worker thread, we need to process it, and put tasks in
        // the business thread
        NioEventLoopGroup business = new NioEventLoopGroup(0, new DefaultThreadFactory("business"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024) // the maximum number of connections
                    .childOption(ChannelOption.TCP_NODELAY, true) // disable for small packets
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // keep the connection alive
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // tcp frame en/decoder
                            pipeline.addLast("tcpFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                            pipeline.addLast("tcpFrameEncoder", new LengthFieldPrepender(4));
                            // request deserialization
                            pipeline.addLast("rpcRequestDecoder", new RpcRequestDeserializer());
                            // response serialization
                            pipeline.addLast("rpcResponseEncoder", new RpcResponseSerializer());
                            // handle the request
                            pipeline.addLast(business, new RequestHandler());
                        }
                    });
        } catch (Exception e) {
            log.error("Netty server start error, msg={}", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            business.shutdownGracefully();
        }
    }
}
