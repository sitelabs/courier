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
 * ��MethodUtil.java��ʵ������������������
 * 
 * @author joe 2013-1-22 ����10:50:29
 */
public class MethodUtil {

    /**
     * ���淽����ִ�н��
     */
    public static void cacheMethod(Object obj, String method, Object[] args, Object result) {

        String key = getCacheMethodKey(obj, method, args);
        cacheMethod(key, result);
    }

    /**
     * ���淽�����
     * 
     * @param key
     * @param result
     */
    public static void cacheMethod(String key, Object result) {
        if (result == null) {
            return;
        }
        RequestParamUtil.addContextParam(key, result);
    }

    /**
     * ��ȡ���淽��key
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
     * �ӻ����л�ȡ�������
     * 
     * @param obj
     * @param method
     * @param args
     * @return
     */
    public static <T> T getCacheMethodResult(Object obj, String method, Object[] args) {
        String key = getCacheMethodKey(obj, method, args);
        return getCacheMethodResult(key);
    }

    /**
     * �ӻ����л�ȡ�������
     * 
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCacheMethodResult(String key) {
        return (T) RequestParamUtil.getContextParam(key);
    }

}
