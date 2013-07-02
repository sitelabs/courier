package com.alibaba.china.courier.fastjson.serializer;

public interface PropertyPreFilter extends SerializeFilter {

    boolean apply(JSONSerializer serializer, Object source, String name);
}
