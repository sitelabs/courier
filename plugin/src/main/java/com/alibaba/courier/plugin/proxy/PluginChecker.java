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
import com.alibaba.courier.plugin.DynamicBean;
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

    public static void check(Object obj) {
        if (obj == null) {
            return;
        }
        String key = CHECKSTR + obj.getClass();
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
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            boolean isDynBeanField = PluginFactory.instance.getDynamicPluginIDs().contains(fieldName);
            if (isDynBeanField) {
                isDynClass = true;
            }

            // 常量不处理
            Class<?> fieldClz = field.getType();
            if (isPrivateType(fieldClz)) {
                continue;
            }
            try {
                // 如果变量已经有值，则不处理
                if (field.get(obj) != null && !isDynBeanField) {
                    continue;
                }
            } catch (Exception e1) {
                continue;
            }
            try {
                // 匹配setXXX方法，如果找到，则说明需要注入插件
                Method method = obj.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), fieldClz);
                if (method == null) {
                    continue;
                }
                // 判断是否是动态bean，如果是动态bean，则替换pluginId，让容器找到真实的插件
                String pluginId = isDynBeanField ? fieldName + ClassProxy.PROXY : fieldName;
                // 只处理动态插件
                Object val = null;
                if (fieldClz.isAssignableFrom(List.class)) {
                    if (isDynBeanField) {
                        // 动态bean不做链式执行，所以此行为非法
                        continue;
                    }
                    val = PluginFactory.instance.getPlugins(pluginId);
                } else {
                    val = PluginFactory.instance.getPlugin(pluginId);
                }
                if (val != null) {
                    try {
                        Object result = DynamicBeanUtil.load((DynamicBean) val);
                        method.invoke(obj, result);
                    } catch (Exception e) {
                        log.error("", e);
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
