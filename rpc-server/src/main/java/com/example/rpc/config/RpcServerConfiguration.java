package com.example.rpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RpcServerConfiguration {
    @Value("${rpc.server.zk.root}")
    private String zkRoot;
    @Value("${rpc.server.zk.addr}")
    private String zkAddr;
    @Value("${rpc.network.port}")
    private int rpcPort;
    @Value("${rpc.server.network.port}")
    private int serverPort;
    @Value("${rpc.server.zk.timeout:10000}")
    private int connectTimeout;
    @Value("${rpc.server.network.address}")
    private String serverAddress;

    public String getServerAddress() {
        return serverAddress;
    }

    public String getZkRoot() {
        return zkRoot;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }
}
