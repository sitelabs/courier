package com.alibaba.china.courier.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import com.alibaba.china.courier.fastjson.parser.DefaultJSONParser;
import com.alibaba.china.courier.fastjson.parser.JSONLexer;
import com.alibaba.china.courier.fastjson.parser.JSONToken;
import com.alibaba.china.courier.fastjson.parser.ParserConfig;
import com.alibaba.china.courier.fastjson.util.FieldInfo;

public class ArrayListStringFieldDeserializer extends FieldDeserializer {

    public ArrayListStringFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo);

    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @Override
    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        ArrayList<Object> list;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            list = null;
        } else {
            list = new ArrayList<Object>();

            ArrayListStringDeserializer.parseArray(parser, list);
        }
        if (object == null) {
            fieldValues.put(fieldInfo.getName(), list);
        } else {
            setValue(object, list);
        }
    }
}
