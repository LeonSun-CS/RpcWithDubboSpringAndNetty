package com.example.rpc.client.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The rpc requests the client sent to the server contains the request id. When the netty client receives a response,
 * it will use the request id to find the corresponding request and set the response to the request. This is done by
 * using a map to store the request id and the corresponding request promise, to distinguish between different requests.
 * Static methods are used to add, get, and remove request promises from the map.
 */
@Slf4j
@Component
public class RpcRequestContainer {
    private static Map<String, RequestPromise> requestPromiseMap = new ConcurrentHashMap<String, RequestPromise>();

    public static void addRequestPromise(String requestId, RequestPromise promise) {
        requestPromiseMap.put(requestId, promise);
    }

    public static RequestPromise getRequestPromise(String requestId) {
        return requestPromiseMap.get(requestId);
    }

    public static void removeRequestPromise(String requestId) {
        requestPromiseMap.remove(requestId);
    }

    private static Map<String, ChannelMapping> channelMappingMap = new ConcurrentHashMap<>();

    public static boolean channelExist(String serverIp, int serverPort) {
        return channelMappingMap.containsKey(serverIp + ":" + serverPort);
    }

    public static void addChannelMapping(ChannelMapping channelMapping) {
        channelMappingMap.put(channelMapping.getIp() + ":" + channelMapping.getPort(), channelMapping);
    }

    public static ChannelMapping getChannelMapping(String serverIp, int serverPort) {
        return channelMappingMap.get(serverIp + ":" + serverPort);
    }

    public static void removeChannelMapping(String serverIp, int serverPort) {
        ChannelMapping channelMapping = channelMappingMap.get(serverIp + ":" + serverPort);
        if (channelMapping != null) {
            channelMapping.getChannel().closeFuture();
            channelMapping.getChannel().eventLoop().shutdownGracefully();
            log.info("Channel closed: {}", channelMapping.getChannel());
            channelMappingMap.remove(serverIp + ":" + serverPort);
        }
    }

    @PreDestroy
    public void destroy() {
        for (Map.Entry<String, ChannelMapping> entry : channelMappingMap.entrySet()) {
            ChannelMapping channelMapping = entry.getValue();
            channelMapping.getChannel().closeFuture();
            channelMapping.getChannel().eventLoop().shutdownGracefully();
            log.info("Channel closed: {}", channelMapping.getChannel());
            channelMappingMap.remove(entry.getKey());
        }
    }
}
