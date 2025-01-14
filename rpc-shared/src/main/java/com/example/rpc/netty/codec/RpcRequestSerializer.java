package com.example.rpc.netty.codec;

import com.example.rpc.messages.RpcRequest;
import com.example.rpc.netty.codec.cache.SchemaCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.util.List;

/**
 * used by the client to serialize RpcRequest objects into byte arrays
 */
public class RpcRequestSerializer extends MessageToMessageEncoder<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequestMessage, List<Object> out) throws Exception {
        RuntimeSchema<RpcRequest> schema = SchemaCache.getSchema(RpcRequest.class);
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            byte[] serialized = ProtostuffIOUtil.toByteArray(rpcRequestMessage, schema, linkedBuffer);
            out.add(serialized);
        }  finally {
            linkedBuffer.clear(); // to prevent memory leak
        }
    }
}
