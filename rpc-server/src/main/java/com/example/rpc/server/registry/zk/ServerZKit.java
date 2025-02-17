package com.example.rpc.server.registry.zk;

import com.example.rpc.server.config.RpcServerConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerZKit {
    @Autowired
    private ZkClient zkClient;

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    public void createRootNode() {
        boolean exists = zkClient.exists(rpcServerConfiguration.getZkRoot());
        if (!exists) {
            zkClient.createPersistent(rpcServerConfiguration.getZkRoot());
        }
    }


    /**
     * for creating service nodes (service interfaces); persistent nodes
     */
    public void createPersistentNode(String path) {
        String pathName = rpcServerConfiguration.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createPersistent(pathName);
        }
    }

    /**
     * for creating provider nodes (service provider info); ephemeral nodes
     * the path includes the interface name and the provider's IP address and port, and other info if
     * necessary
     */
    public void createNode(String path) {
        String pathName = rpcServerConfiguration.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createEphemeral(pathName);
        }
    }
}
