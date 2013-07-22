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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.china.courier.util.Utils.ApplicationParamUtil;
import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.DynamicBeanUtil;
import com.alibaba.courier.plugin.PluginFactory;

/**
 * 插件校验器
 * 
 * <pre>
 * 1、检测包含有动态bean属性的类
 * 2、对动态Bean进行request级别的赋值，以确保动态Bean的数据都是实时的
 * </pre>
 * 
 * @author joe 2013-7-2 下午2:56:22
 */
public class PluginChecker {

    private static final Log    log      = LogFactory.getLog(PluginChecker.class);

    private static final String CHECKSTR = "checker://";

    public static void check(Object instance, Object proxy) {
        if (instance == null) {
            return;
        }
        if (needLoadproxy(instance, proxy)) {
            return;
        }
        String key = CHECKSTR + proxy.getClass();
        // 是否有动态bean参数
        Boolean isDynClass = ApplicationParamUtil.getContextParam(key);
        if (isDynClass != null && !isDynClass) {
            return;
        }
        // 是否执行过动态参数注入
        Boolean checked = RequestParamUtil.getContextParam(key);
        if (checked != null && checked) {
            return;
        }
        isDynClass = false;// 是否包含了动态Bean属性，如果包含，每次都需要注入
        // Field[] fields = obj.getClass().getDeclaredFields();
        Class<?> clazz = proxy.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                checkField(proxy, field, isDynClass);
            }
        }

        ApplicationParamUtil.addContextParam(key, isDynClass);
        RequestParamUtil.addContextParam(key, true);
    }

    /**
     * 检查代理类对象是否需要重新加载
     * 
     * <pre>
     * 判断条件：
     * 1、代理对象为空
     * 2、该实例是动态Bean
     * </pre>
     * 
     * @param instance
     * @param proxy
     */
    private static boolean needLoadproxy(Object instance, Object proxy) {
        if (proxy == null) {
            try {
                String pluginID = (String) ClassProxy.getFieldVal(ClassProxy.PLUGINID, instance);
                if (pluginID != null && PluginFactory.instance.getDynamicPluginIDs().contains(pluginID)) {
                    DynamicBeanUtil.getProxy(pluginID, instance);
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * 检查每个变量
     * 
     * @param obj
     * @param field
     * @param isDynClass
     */
    private static void checkField(Object obj, Field field, Boolean isDynClass) {
        field.setAccessible(true);
        String fieldName = field.getName();
        boolean isDynBeanField = PluginFactory.instance.getDynamicPluginIDs().contains(fieldName);
        if (isDynBeanField) {
            isDynClass = true;
        }

        // 常量不处理
        Class<?> fieldClz = field.getType();
        if (isPrivateType(fieldClz)) {
            return;
        }
        try {
            // 如果变量已经有值，则不处理
            if (field.get(obj) != null && !isDynBeanField) {
                return;
            }
        } catch (Exception e1) {
            return;
        }
        try {
            // 匹配setXXX方法，如果找到，则说明需要注入插件
            Method method = obj.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), fieldClz);
            if (method == null) {
                return;
            }
            // 只处理动态插件
            if (!fieldClz.isAssignableFrom(List.class)) {
                try {
                    Object result = DynamicBeanUtil.load(fieldName);
                    method.invoke(obj, result);
                } catch (Exception e) {
                    log.error("", e);
                }
            }

        } catch (Exception e) {
        }

    }

    /**
     * 是否私有原生类型
     * 
     * @param clz
     * @return
     */
    public static boolean isPrivateType(Class<?> clz) {
        return clz == int.class || clz == short.class || clz == boolean.class || clz == long.class
               || clz == float.class || clz == double.class || clz == byte.class || clz.getName().startsWith("java.");
    }

}
