/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.china.courier.util.ObjectInvoker;
import com.alibaba.china.courier.util.Utils.ApplicationParamUtil;
import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.PluginFactory;

/**
 * ���У����
 * 
 * @author joe 2013-7-2 ����2:56:22
 */
public class PluginChecker {

    private static final String CHECKSTR = "checker://";

    public static void check(Object obj) {
        String key = CHECKSTR + obj.getClass();
        // �Ƿ��ж�̬bean����
        Boolean checked = ApplicationParamUtil.getContextParam(key);
        if (checked != null && !checked) {
            return;
        }
        // �Ƿ�ִ�й���̬����ע��
        checked = RequestParamUtil.getContextParam(key);
        if (checked != null && checked) {
            return;
        }
        boolean isDynClass = false;
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();

            boolean isDynBean = PluginFactory.instance.getDynamicPluginIDs().contains(fieldName);
            if (!isDynBean) {
                continue;
            }
            isDynClass = true;

            Class<?> fieldClz = field.getType();
            if (isPrivateType(fieldClz)) {
                continue;
            }

            try {
                Method method = obj.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), fieldClz);
                if (method == null) {
                    continue;
                }
                // �ж��Ƿ��Ƕ�̬bean������Ƕ�̬bean�����滻pluginId���������ҵ���ʵ�Ĳ��
                String pluginId = fieldName + ClassProxy.PROXY;
                // ֻ����̬���
                Object val = null;
                if (fieldClz.isAssignableFrom(List.class)) {
                    val = PluginFactory.instance.getPlugins(pluginId);
                } else {
                    val = PluginFactory.instance.getPlugin(pluginId);
                }
                if (val != null) {
                    try {
                        Field proxy = val.getClass().getDeclaredField(ClassProxy.PROXY);
                        Object proxyObj = proxy.get(val);
                        Object result = ObjectInvoker.handler(val.getClass().getName() + ".load", proxyObj, "load");

                        method.invoke(obj, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }

            } catch (Exception e) {
            }
        }
        ApplicationParamUtil.addContextParam(key, isDynClass);
        RequestParamUtil.addContextParam(key, true);
    }

    private static boolean isPrivateType(Class<?> clz) {
        return clz == int.class || clz == short.class || clz == boolean.class || clz == long.class
               || clz == float.class || clz == double.class || clz == byte.class || clz.getName().startsWith("java.");
    }

}
