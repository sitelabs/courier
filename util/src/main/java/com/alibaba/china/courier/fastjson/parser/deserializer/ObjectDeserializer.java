package com.alibaba.china.courier.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.alibaba.china.courier.fastjson.parser.DefaultJSONParser;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
    
    int getFastMatchToken();
}
