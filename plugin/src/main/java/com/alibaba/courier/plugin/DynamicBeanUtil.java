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
import com.google.common.collect.Maps;

/**
 * 动态类的辅助工具类
 * 
 * @author joe 2013年7月7日 下午9:46:56
 */
public class DynamicBeanUtil {

    public static final String                 loadMethodName     = "load";

    private static final String                proxyCacheStr      = ".load";
    private static final String                realCacheStr       = ".realload";

    private static final Map<String, Class<?>> cacheMethodReturns = Maps.newConcurrentMap();

    /**
     * 加载动态Bean
     * 
     * @param bean
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object load(String pluginID) {
        DynamicBean bean = PluginFactory.instance.getPlugin(pluginID);
        if (bean == null) {
            return null;
        }

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
            // 获取动态Bean的load方法 返回类型
            Class<?> proxyClass = ClassProxy.create(returnType);
            result = proxyClass.newInstance();
            ClassProxy.setPluginIDField(result, pluginID);
        } catch (Exception e) {
        }

        RequestParamUtil.addContextParam(key, result);
        return result;
    }

    /**
     * 获取真实的结果
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
     * 获取代理对象
     * 
     * @param pluginID
     * @return
     */
    public static Object getProxy(String pluginID, Object instance) {

        String key = pluginID + realCacheStr;
        if (RequestParamUtil.getContextParams().containsKey(key)) {
            Object result = RequestParamUtil.getContextParam(key);
            if (instance != null) {
                Object proxy = ClassProxy.getFieldVal(ClassProxy.PROXY, result);
                if (proxy != null) {
                    ClassProxy.setProxyField(instance, proxy);
                }
                return instance;
            }
            return result;
        }
        Object result = getResult(pluginID);

        if (instance == null) {
            try {
                Class<?> proxyClass = ClassProxy.create(result.getClass());
                instance = proxyClass.newInstance();
            } catch (Exception e) {
            }
        }
        ClassProxy.setProxyField(instance, result);
        RequestParamUtil.addContextParam(key, instance);
        return instance;
    }
}
