/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import java.lang.reflect.Method;

import com.alibaba.china.courier.fastjson.util.TypeUtils;

/**
 * ����ִ���ߣ����ڷ���ִ��
 * 
 * @author joe 2013-6-8 ����3:20:57
 */
public class ObjectInvoker {

    /**
     * ִ�ж����ĳһ������
     * 
     * @param uniqueName Ψһ��name
     * @param obj ʵ������
     * @param args ���� ��һ��Ϊ������
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <R> R handler(String uniqueName, Object obj, Object... args) throws Exception {

        if (args == null) {
            return null;
        }
        String methodName = (String) args[0];
        Object[] params = null;
        if (args.length > 1 && !(args.length == 2 && args[1] == null)) {
            params = new Object[args.length - 1];
            for (int i = 0; i < params.length; i++) {
                params[i] = args[i + 1];
            }
        }

        // �ӵ�ǰ�̻߳�����ȡ���������������ظ�����
        R cacheResult = (R) MethodUtil.getCacheMethodResult(uniqueName, methodName, params);
        if (cacheResult != null) {
            return cacheResult;
        }

        if (obj == null) {
            return null;
        }

        Method method = null;

        Method[] mts = obj.getClass().getDeclaredMethods();
        for (Method _method : mts) {
            int len = params == null ? 0 : params.length;
            if (_method.getName().equals(methodName) && len == _method.getParameterTypes().length) {
                method = _method;
                break;
            }
        }
        if (method == null) {
            return null;
        }
        R r = null;
        Class<?>[] types = method.getParameterTypes();
        if (types == null) {
            r = (R) method.invoke(obj, params);
        } else {
            // ����fastjson��Beanת��������ת�� ��fastjosn̫���ˣ�
            Object[] parameterValues = new Object[types.length];
            for (int i = 0; i < types.length; i++) {

                parameterValues[i] = TypeUtils.castToJavaBean(params[i], types[i]);
            }
            r = (R) method.invoke(obj, parameterValues);
        }
        MethodUtil.cacheMethod(uniqueName, methodName, params, r);
        return r;

    }

}
