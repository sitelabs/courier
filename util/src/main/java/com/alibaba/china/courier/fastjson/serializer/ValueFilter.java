package com.alibaba.china.courier.fastjson.serializer;

public interface ValueFilter extends SerializeFilter {

    Object process(Object source, String name, Object value);
}
