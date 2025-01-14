package com.example.rpc.netty.codec.cache;

import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a cache to store the schema of the service class, to avoid creating the schema every time
 * messages are serialized or deserialized.
 */
@Component
public class SchemaCache {
    private static final ConcurrentHashMap<Class<?>, RuntimeSchema<?>> schemaCache = new ConcurrentHashMap<>();

    public static <T> RuntimeSchema<T> getSchema(Class<T> cls) {
        @SuppressWarnings("unchecked")
        RuntimeSchema<T> schema = (RuntimeSchema<T>) schemaCache.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            schemaCache.put(cls, schema);
        }
        return schema;
    }
}
