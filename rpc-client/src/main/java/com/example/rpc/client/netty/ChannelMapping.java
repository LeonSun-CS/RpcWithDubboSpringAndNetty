package com.example.rpc.client.netty;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ChannelMapping {
    // rpc server ip
    private String ip = "127.0.0.1";
    // rpc server port
    private int port;
    // channel that client builds with rpc server
    private Channel channel;

    public ChannelMapping(String ip, int port, Channel channel) {
        this.ip = ip;
        this.port = port;
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelMapping that = (ChannelMapping) o;
        return port == that.port && ip.equals(that.ip) ;
    }

}
