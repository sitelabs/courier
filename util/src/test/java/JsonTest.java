import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.china.courier.fastjson.JSON;
import com.alibaba.china.courier.model.JsonResult;

/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * 类JsonTest.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013-5-13 下午12:36:09
 */
public class JsonTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Map m = new HashMap();

        ArrayList<String> a = new ArrayList<String>();
        a.add("1");
        a.add("2");
        m.put("list", a);
        String as = JSON.toJSONString(m);
        System.out.println(as);
        Map ts = JSON.parseObject(as, Map.class);
        System.out.println(ts);

        JsonResult r = new JsonResult(true);
        System.out.println(JSON.toJSONString(r));

    }

}
