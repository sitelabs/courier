package com.alibaba.china.courier.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import com.alibaba.china.courier.fastjson.JSONObject;

public class JSONLibDataFormatSerializer implements ObjectSerializer {

    public JSONLibDataFormatSerializer(){
    }

    @SuppressWarnings("deprecation")
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
    	if (object == null) {
    		serializer.getWriter().writeNull();
    		return;
    	}
    	
        Date date = (Date) object;
       
        JSONObject json = new JSONObject();
        json.put("date", date.getDate());
        json.put("day", date.getDay());
        json.put("hours", date.getHours());
        json.put("minutes", date.getMinutes());
        json.put("month", date.getMonth());
        json.put("seconds", date.getSeconds());
        json.put("time", date.getTime());
        json.put("timezoneOffset", date.getTimezoneOffset());
        json.put("year", date.getYear());

        serializer.write(json);
    }
}
