package com.example.rpc.server.netty;

import com.example.rpc.netty.codec.RpcRequestDeserializer;
import com.example.rpc.netty.codec.RpcResponseSerializer;
import com.example.rpc.server.config.RpcServerConfiguration;
import com.example.rpc.server.netty.handler.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RpcNettyServer implements RpcServer {
    private final Logger logger = LoggerFactory.getLogger(RpcNettyServer.class);

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    /**
     * Start the server. As a netty server, it should bind to a port and listen to the incoming requests.
     * The server will be started by the RpcServerRunner class, after the service registration.
     * TCP frame: LengthFieldBasedFrame
     * Serialization: Protostuff
     *
     */
    @Override
    public void start() {
        System.out.println("********************************Starting the Netty server");
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
            System.out.println("********************************Entering the try block");

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
                            pipeline.addLast("tcpFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("tcpFrameEncoder", new LengthFieldPrepender(4));
                            // request deserialization
                            pipeline.addLast("rpcRequestDecoder", new RpcRequestDeserializer());
                            // response serialization
                            pipeline.addLast("rpcResponseEncoder", new RpcResponseSerializer());
                            // handle the request
                            pipeline.addLast(business, "requestHandler", new RequestHandler());
                        }
                    });
            System.out.println("********************************Server starting on port " + rpcServerConfiguration.getRpcPort());
            ChannelFuture future = serverBootstrap.bind(rpcServerConfiguration.getRpcPort()).sync();
            System.out.println("********************************Netty server port binding successful: " + rpcServerConfiguration.getRpcPort());
            logger.info("Netty server started on port {}", rpcServerConfiguration.getRpcPort());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Netty server start error, msg={}", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            business.shutdownGracefully();
        }
    }
}
