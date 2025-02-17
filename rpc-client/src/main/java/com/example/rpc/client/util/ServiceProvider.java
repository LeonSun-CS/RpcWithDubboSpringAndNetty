package com.example.rpc.client.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class aims to store the information of service provider such as service name, server IP, RPC port, network port,
 * timeout, and weight, after retrieving those information from the service registry.
 * More information can be added, but for this project, these are the basic information needed.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceProvider {
    private String serviceName;
    private String serverIp;
    private int rpcPort;
    private int networkPort;
    private long timeout;
    private int weight; // for load balancing
}
