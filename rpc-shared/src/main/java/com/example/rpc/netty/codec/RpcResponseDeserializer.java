package com.example.rpc.netty.codec;

import com.example.rpc.messages.RpcResponse;
import com.example.rpc.netty.codec.cache.SchemaCache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.protostuff.ProtostuffIOUtil;

import java.util.List;

/**
 * used by the client to deserialize the response from the server
 */
public class RpcResponseDeserializer extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        RpcResponse rpcResponse = new RpcResponse();
        ProtostuffIOUtil.mergeFrom(bytes, rpcResponse, SchemaCache.getSchema(RpcResponse.class));
        list.add(rpcResponse);
    }
}
