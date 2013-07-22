/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.lang.reflect.Method;

import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.proxy.ClassProxy;

/**
 * 动态类的辅助工具类
 * 
 * @author joe 2013年7月7日 下午9:46:56
 */
public class DynamicBeanUtil {

    public static final String  loadMethodName = "load";

    private static final String proxyCacheStr  = ".load";
    private static final String realCacheStr   = ".realload";

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
        String key = bean.getClass().getName() + proxyCacheStr;
        Object result = RequestParamUtil.getContextParam(key);
        if (result != null) {
            return result;
        }

        try {
            // 获取动态Bean的load方法 返回类型
            Method method = bean.getClass().getMethod(loadMethodName, null);
            Class<?> returnType = method.getReturnType();
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
        Object result = RequestParamUtil.getContextParam(key);
        if (result != null) {
            return result;
        }
        result = getResult(pluginID);

        Class<?> proxyClass = ClassProxy.create(result.getClass());
        if (instance == null) {
            try {
                instance = proxyClass.newInstance();
            } catch (Exception e) {
            }
        }
        ClassProxy.setProxyField(instance, result);

        RequestParamUtil.addContextParam(key, instance);
        return instance;
    }
}
