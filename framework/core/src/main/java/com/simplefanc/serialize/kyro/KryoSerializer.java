package com.simplefanc.serialize.kyro;

import com.simplefanc.serialize.Serializer;

/**
 * @author chenfan
 * @date 2022/10/18 16:40
 **/
public class KryoSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
