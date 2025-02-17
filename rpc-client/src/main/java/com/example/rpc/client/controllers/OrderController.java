package com.example.rpc.client.controllers;

import com.example.rpc.client.annotations.RpcServiceProxy;
import com.example.rpc.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping("/order")
public class OrderController {

    @RpcServiceProxy
    private OrderService orderService;

    @GetMapping("/get{order-id}")
    public String getOrder(@PathVariable("order-id") String orderId) {
        System.out.println("orderId: " + orderId);
        return orderService.getOrderInfo(orderId);
    }

}
