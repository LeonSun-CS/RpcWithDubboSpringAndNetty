package com.example.rpc.server.netty.codec;

import com.example.rpc.server.messages.RpcRequest;
import com.example.rpc.server.netty.codec.cache.SchemaCache;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.util.List;

/**
 * used by the server to deserialize byte arrays into RpcRequest objects
 */
public class RpcRequestDeserializer extends MessageToMessageDecoder<ByteBuf> {
// messagetomessagedecoder is good for decoding complete messages and doesn't handle fragmentation
// ByteToMessageDecoder is good for decoding messages that are fragmented across multiple ByteBufs

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        RuntimeSchema<RpcRequest> schema = SchemaCache.getSchema(RpcRequest.class);
        RpcRequest rpcRequest = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, rpcRequest, schema);
        list.add(rpcRequest);
    }
}
