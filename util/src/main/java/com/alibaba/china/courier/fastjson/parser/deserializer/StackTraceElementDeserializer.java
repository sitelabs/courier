package com.alibaba.china.courier.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.alibaba.china.courier.fastjson.JSON;
import com.alibaba.china.courier.fastjson.JSONException;
import com.alibaba.china.courier.fastjson.parser.DefaultJSONParser;
import com.alibaba.china.courier.fastjson.parser.Feature;
import com.alibaba.china.courier.fastjson.parser.JSONLexer;
import com.alibaba.china.courier.fastjson.parser.JSONToken;

public class StackTraceElementDeserializer implements ObjectDeserializer {

    public final static StackTraceElementDeserializer instance = new StackTraceElementDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }

        if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA) {
            throw new JSONException("syntax error: " + JSONToken.name(lexer.token()));
        }

        String declaringClass = null;
        String methodName = null;
        String fileName = null;
        int lineNumber = 0;

        for (;;) {
            // lexer.scanSymbol
            String key = lexer.scanSymbol(parser.getSymbolTable());

            if (key == null) {
                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                }
                if (lexer.token() == JSONToken.COMMA) {
                    if (lexer.isEnabled(Feature.AllowArbitraryCommas)) {
                        continue;
                    }
                }
            }

            lexer.nextTokenWithColon(JSONToken.LITERAL_STRING);
            if (key == "className") {
                if (lexer.token() == JSONToken.NULL) {
                    declaringClass = null;
                } else if (lexer.token() == JSONToken.LITERAL_STRING) {
                    declaringClass = lexer.stringVal();
                } else {
                    throw new JSONException("syntax error");
                }
            } else if (key == "methodName") {
                if (lexer.token() == JSONToken.NULL) {
                    methodName = null;
                } else if (lexer.token() == JSONToken.LITERAL_STRING) {
                    methodName = lexer.stringVal();
                } else {
                    throw new JSONException("syntax error");
                }
            } else if (key == "fileName") {
                if (lexer.token() == JSONToken.NULL) {
                    fileName = null;
                } else if (lexer.token() == JSONToken.LITERAL_STRING) {
                    fileName = lexer.stringVal();
                } else {
                    throw new JSONException("syntax error");
                }
            } else if (key == "lineNumber") {
                if (lexer.token() == JSONToken.NULL) {
                    lineNumber = 0;
                } else if (lexer.token() == JSONToken.LITERAL_INT) {
                    lineNumber = lexer.intValue();
                } else {
                    throw new JSONException("syntax error");
                }
            } else if (key == "nativeMethod") {
                if (lexer.token() == JSONToken.NULL) {
                    lexer.nextToken(JSONToken.COMMA);
                } else if (lexer.token() == JSONToken.TRUE) {
                    lexer.nextToken(JSONToken.COMMA);
                } else if (lexer.token() == JSONToken.FALSE) {
                    lexer.nextToken(JSONToken.COMMA);
                } else {
                    throw new JSONException("syntax error");
                }
            } else if (key == JSON.DEFAULT_TYPE_KEY) {
                if (lexer.token() == JSONToken.NULL) {
                    // skip
                } else if (lexer.token() == JSONToken.LITERAL_STRING) {
                    String elementType = lexer.stringVal();
                    if (!elementType.equals("java.lang.StackTraceElement")) {
                        throw new JSONException("syntax error : " + elementType);    
                    }
                } else {
                    throw new JSONException("syntax error");
                }
            } else {
                throw new JSONException("syntax error : " + key);
            }

            if (lexer.token() == JSONToken.COMMA) {
                continue;
            }

            if (lexer.token() == JSONToken.RBRACE) {
                lexer.nextToken(JSONToken.COMMA);
                break;
            }

        }
        return (T) new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
