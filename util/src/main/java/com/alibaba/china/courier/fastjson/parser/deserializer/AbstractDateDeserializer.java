package com.alibaba.china.courier.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.alibaba.china.courier.fastjson.JSON;
import com.alibaba.china.courier.fastjson.JSONException;
import com.alibaba.china.courier.fastjson.parser.DefaultJSONParser;
import com.alibaba.china.courier.fastjson.parser.Feature;
import com.alibaba.china.courier.fastjson.parser.JSONScanner;
import com.alibaba.china.courier.fastjson.parser.JSONToken;
import com.alibaba.china.courier.fastjson.util.TypeUtils;

public abstract class AbstractDateDeserializer implements ObjectDeserializer {

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONScanner lexer = (JSONScanner) parser.getLexer();

        Object val;
        if (lexer.token() == JSONToken.LITERAL_INT) {
            val = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);
        } else if (lexer.token() == JSONToken.LITERAL_STRING) {
            String strVal = lexer.stringVal();
            val = strVal;
            lexer.nextToken(JSONToken.COMMA);
            
            if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
                JSONScanner iso8601Lexer = new JSONScanner(strVal);
                if (iso8601Lexer.scanISO8601DateIfMatch()) {
                    val = iso8601Lexer.getCalendar().getTime();
                }
            }
        } else if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            val = null;
        } else if (lexer.token() == JSONToken.LBRACE) {
            lexer.nextToken();
            
            String key;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                key = lexer.stringVal();
                
                if (JSON.DEFAULT_TYPE_KEY.equals(key)) {
                    lexer.nextToken();
                    parser.accept(JSONToken.COLON);
                    
                    String typeName = lexer.stringVal();
                    Class<?> type = TypeUtils.loadClass(typeName);
                    if (type != null) {
                        clazz = type;
                    }
                    
                    parser.accept(JSONToken.LITERAL_STRING);
                    parser.accept(JSONToken.COMMA);
                }
                
                lexer.nextTokenWithColon(JSONToken.LITERAL_INT);
            } else {
                throw new JSONException("syntax error");
            }
            
            long timeMillis;
            if (lexer.token() == JSONToken.LITERAL_INT) {
                timeMillis = lexer.longValue();
                lexer.nextToken();
            } else {
                throw new JSONException("syntax error : " + lexer.tokenName());
            }
            
            val = timeMillis;
            
            parser.accept(JSONToken.RBRACE);
        } else if (parser.getResolveStatus() == DefaultJSONParser.TypeNameRedirect) {
            parser.setResolveStatus(DefaultJSONParser.NONE);
            parser.accept(JSONToken.COMMA);

            if (lexer.token() == JSONToken.LITERAL_STRING) {
                if (!"val".equals(lexer.stringVal())) {
                    throw new JSONException("syntax error");
                }
                lexer.nextToken();
            } else {
                throw new JSONException("syntax error");
            }

            parser.accept(JSONToken.COLON);

            val = parser.parse();

            parser.accept(JSONToken.RBRACE);
        } else {
            val = parser.parse();
        }

        return (T) cast(parser, clazz, fieldName, val);
    }

    protected abstract <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object value);
}
