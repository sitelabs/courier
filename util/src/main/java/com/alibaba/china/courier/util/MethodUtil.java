/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import com.alibaba.china.courier.fastjson.JSON;
import com.alibaba.china.courier.util.Utils.GolbalConstants;
import com.alibaba.china.courier.util.Utils.RequestParamUtil;

/**
 * 类MethodUtil.java的实现描述：函数工具类
 * 
 * @author joe 2013-1-22 上午10:50:29
 */
public class MethodUtil {

    /**
     * 缓存方法的执行结果
     */
    public static void cacheMethod(Object obj, String method, Object[] args, Object result) {

        String key = getCacheMethodKey(obj, method, args);
        RequestParamUtil.addContextParam(key, result);
    }

    /**
     * 获取缓存方法key
     * 
     * @param obj
     * @param method
     * @param args
     * @return
     */
    public static String getCacheMethodKey(Object obj, String method, Object[] args) {
        StringBuffer sb = new StringBuffer();
        if (obj instanceof String) {
            sb.append(obj);
        } else {
            sb.append(obj.getClass().getName());
        }
        sb.append(GolbalConstants.URI_SPLIT);
        sb.append(method);
        sb.append(GolbalConstants.URI_SPLIT);
        if (args != null) {
            String argsJson = JSON.toJSONString(args);
            sb.append(argsJson);
        }
        return sb.toString();
    }

    /**
     * 从缓存中获取方法结果
     * 
     * @param obj
     * @param method
     * @param args
     * @return
     */
    public static <T> T getCacheMethodResult(Object obj, String method, Object[] args) {
        String key = getCacheMethodKey(obj, method, args);
        return RequestParamUtil.getContextParam(key);
    }

}
