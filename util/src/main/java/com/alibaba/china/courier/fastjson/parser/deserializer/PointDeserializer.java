package com.alibaba.china.courier.fastjson.parser.deserializer;

import java.awt.Point;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import com.alibaba.china.courier.fastjson.JSON;
import com.alibaba.china.courier.fastjson.JSONException;
import com.alibaba.china.courier.fastjson.parser.DefaultJSONParser;
import com.alibaba.china.courier.fastjson.parser.JSONScanner;
import com.alibaba.china.courier.fastjson.parser.JSONToken;

public class PointDeserializer implements AutowiredObjectDeserializer {

    public final static PointDeserializer instance = new PointDeserializer();
    
    

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONScanner lexer = (JSONScanner) parser.getLexer();
        
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return null;
        }
        
        if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA) {
            throw new JSONException("syntax error");
        }
        lexer.nextToken();

        int x = 0, y = 0;
        for (;;) {
            if (lexer.token() == JSONToken.RBRACE) {
                lexer.nextToken();
                break;
            }

            String key;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                key = lexer.stringVal();
                
                if (JSON.DEFAULT_TYPE_KEY.equals(key)) {
                    parser.acceptType("java.awt.Point");
                    continue;
                }
                
                lexer.nextTokenWithColon(JSONToken.LITERAL_INT);
            } else {
                throw new JSONException("syntax error");
            }
            
            int val;
            if (lexer.token() == JSONToken.LITERAL_INT) {
                val = lexer.intValue();
                lexer.nextToken();
            } else {
                throw new JSONException("syntax error : " + lexer.tokenName());
            }

            if (key.equalsIgnoreCase("x")) {
                x = val;
            } else if (key.equalsIgnoreCase("y")) {
                y = val;
            } else {
                throw new JSONException("syntax error, " + key);
            }

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(JSONToken.LITERAL_STRING);
            }
        }

        return (T) new Point(x, y);
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }

    public Set<Type> getAutowiredFor() {
        return Collections.<Type>singleton(Point.class);
    }
}
