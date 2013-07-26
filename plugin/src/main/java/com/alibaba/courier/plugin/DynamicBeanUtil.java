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
 * 动态类的辅助工具类
 * 
 * @author joe 2013年7月7日 下午9:46:56
 */
public class DynamicBeanUtil {

    public static final String               loadMethodName     = "load";

    private static final String              realCacheStr       = ".realload";

    private static final Map<String, Object> cacheMethodReturns = Maps.newConcurrentMap();

    /**
     * 加载动态Bean
     * 
     * @param bean
     * @return
     */
    public static Object load(String pluginID) {
        Object bean = PluginFactory.instance.getPlugin(pluginID);
        if (bean == null) {
            return null;
        }
        if (bean instanceof DynamicBean) {
            Object result = cacheMethodReturns.get(pluginID);
            if (result != null) {
                return result;
            }
            try {
                Method method = bean.getClass().getMethod(loadMethodName, null);
                // 获取动态Bean的load方法 返回类型
                Class<?> proxyClass = ClassProxy.create(method.getReturnType(), pluginID, false);
                result = proxyClass.newInstance();
                ClassProxy.setPluginIDField(result, pluginID);
                cacheMethodReturns.put(pluginID, result);
                return result;
            } catch (Exception e1) {
                return null;
            }
        } else {
            return bean;
        }
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
    @SuppressWarnings("unchecked")
    public static Object getProxy(String pluginID) {

        String key = pluginID + realCacheStr;

        Map<String, Object> cache = (Map<String, Object>) RequestParamUtil.getContextParam(realCacheStr);
        if (cache == null) {
            cache = Maps.newHashMap();
            RequestParamUtil.addContextParam(realCacheStr, cache);
        }

        if (cache.containsKey(key)) {
            Object result = cache.get(key);
            return result;
        }
        Object result = getResult(pluginID);
        PluginChecker.check(result);
        // ClassProxy.setProxyField(instance, result);
        cache.put(key, result);
        return result;
    }

    /**
     * 判断是否是动态Bean
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
