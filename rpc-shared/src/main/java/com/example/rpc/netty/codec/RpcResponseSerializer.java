package com.example.rpc.netty.codec;

import com.example.rpc.messages.RpcResponse;
import com.example.rpc.netty.codec.cache.SchemaCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.util.List;

/**
 * used by the server to serialize the response message
 */
public class RpcResponseSerializer extends MessageToMessageEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, List<Object> list) throws Exception {
        RuntimeSchema<RpcResponse> schema = SchemaCache.getSchema(RpcResponse.class);
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            byte[] serialized = ProtostuffIOUtil.toByteArray(rpcResponse, schema, linkedBuffer);
            list.add(serialized);
        }  finally {
            linkedBuffer.clear(); // to prevent memory leak
        }
    }
}
