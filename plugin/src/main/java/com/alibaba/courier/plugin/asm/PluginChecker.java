/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.PluginFactory;

/**
 * 插件校验器
 * 
 * @author joe 2013-7-2 下午2:56:22
 */
public class PluginChecker {

    private static final String CHECKSTR = "checker://";

    public static void check(Object obj) {
        String key = CHECKSTR + obj.getClass();
        Boolean checked = RequestParamUtil.getContextParam(key);
        if (checked != null && checked) {
            return;
        }

        RequestParamUtil.addContextParam("", obj);

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldClz = field.getType();
            if (isPrivateType(fieldClz)) {
                continue;
            }
            try {
                Method method = obj.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), fieldClz);
                // 只处理动态插件
                if (PluginFactory.instance.getDynamicPluginIDs().contains(fieldName)) {
                    Object val = null;
                    if (fieldClz.isAssignableFrom(List.class)) {
                        val = PluginFactory.instance.getPlugins(fieldName);
                    } else {
                        val = PluginFactory.instance.getPlugin(fieldName);
                    }
                    if (val != null) {
                        method.invoke(obj, val);
                    }
                }
            } catch (Exception e) {
            }
        }
        RequestParamUtil.addContextParam(CHECKSTR, true);
    }

    private static boolean isPrivateType(Class<?> clz) {
        return clz == int.class || clz == short.class || clz == boolean.class || clz == long.class
               || clz == float.class || clz == double.class || clz == byte.class || clz.getName().startsWith("java.");
    }

}
