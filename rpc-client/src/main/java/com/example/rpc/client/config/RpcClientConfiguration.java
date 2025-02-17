package com.example.rpc.client.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcClientConfiguration {
    @Value("${rpc.client.zk.root}")
    private String zkRoot;

    @Value("${rpc.client.zk.addr}")
    private String zkAddr;

    @Value("${server.port}")
    private String znsClientPort;

    @Value("${rpc.client.api.package}")
    private String rpcClientApiPackage;

    @Value("${rpc.cluster.strategy}")
    private String rpcClientClusterStrategy;

    @Value("${rpc.client.zk.timeout}")
    private Integer connectTimeout;

    public String getZkRoot() {
        return zkRoot;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public String getZnsClientPort() {
        return znsClientPort;
    }

    public String getRpcClientApiPackage() {
        return rpcClientApiPackage;
    }

    public String getRpcClientClusterStrategy() {
        return rpcClientClusterStrategy;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }
}
