package com.example.rpc.serviceimpls;

import com.example.rpc.annotations.MyRpcService;
import com.example.rpc.config.RpcServerConfiguration;
import com.example.rpc.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@MyRpcService(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    RpcServerConfiguration serverConfiguration;

    @Override
    public String getOrderInfo(String orderId) {
        return serverConfiguration.getServerPort() +"---"+serverConfiguration.getRpcPort()+ ": Congratulations, The RPC call succeeded, orderNo is " + orderId;
    }
}


