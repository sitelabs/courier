/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.lang.reflect.Method;
import java.util.Map;

import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.proxy.ClassProxy;
import com.alibaba.courier.plugin.proxy.PluginChecker;
import com.google.common.collect.Maps;

/**
 * ��̬��ĸ���������
 * 
 * @author joe 2013��7��7�� ����9:46:56
 */
public class DynamicBeanUtil {

    public static final String                 loadMethodName     = "load";

    private static final String                proxyCacheStr      = ".load";
    private static final String                realCacheStr       = ".realload";

    private static final Map<String, Class<?>> cacheMethodReturns = Maps.newConcurrentMap();

    /**
     * ���ض�̬Bean
     * 
     * @param bean
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object load(String pluginID) {
        Object bean = PluginFactory.instance.getPlugin(pluginID);
        if (bean == null) {
            return null;
        }
        if (bean instanceof DynamicBean) {

            Class<?> returnType = cacheMethodReturns.get(pluginID);
            if (returnType == null) {
                try {
                    Method method = bean.getClass().getMethod(loadMethodName, null);
                    returnType = method.getReturnType();
                    cacheMethodReturns.put(pluginID, returnType);
                } catch (Exception e1) {
                    return null;
                }
            }

            String key = returnType.getName() + proxyCacheStr;
            Object result = RequestParamUtil.getContextParam(key);
            if (result != null) {
                return result;
            }
            try {
                // ��ȡ��̬Bean��load���� ��������
                Class<?> proxyClass = ClassProxy.create(returnType, pluginID, false);
                result = proxyClass.newInstance();
                ClassProxy.setPluginIDField(result, pluginID);
            } catch (Exception e) {
            }

            RequestParamUtil.addContextParam(key, result);
            return result;
        } else {
            return bean;
        }
    }

    /**
     * ��ȡ��ʵ�Ľ��
     * 
     * @param pluginID
     * @return
     */
    private static Object getResult(String pluginID) {

        DynamicBean<?> bean = PluginFactory.instance.getPlugin(pluginID);
        if (bean == null) {
            return null;
        }
        Object result = bean.load();
        return result;
    }

    /**
     * ��ȡ�������
     * 
     * @param pluginID
     * @return
     */
    public static Object getProxy(String pluginID) {

        String key = pluginID + realCacheStr;
        if (RequestParamUtil.getContextParams().containsKey(key)) {
            Object result = RequestParamUtil.getContextParam(key);
            return result;
        }
        Object result = getResult(pluginID);
        Object instance = null;
        try {
            Class<?> proxyClass = ClassProxy.create(result.getClass(), pluginID, false);
            instance = proxyClass.newInstance();
        } catch (Exception e) {
        }
        PluginChecker.check(instance, result);
        // ClassProxy.setProxyField(instance, result);
        RequestParamUtil.addContextParam(key, result);
        return result;
    }

    /**
     * �ж��Ƿ��Ƕ�̬Bean
     * 
     * @param cls
     * @return
     */
    public static boolean isDynamicBean(Class<?> cls) {
        Class<?>[] interfaces = cls.getInterfaces();
        if (interfaces != null) {
            for (Class<?> class1 : interfaces) {
                if (class1.equals(DynamicBean.class)) {
                    return true;
                }
            }
        }
        return false;
    }
}
