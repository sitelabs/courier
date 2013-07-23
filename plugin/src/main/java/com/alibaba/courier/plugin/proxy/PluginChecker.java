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
 * ���У����
 * 
 * <pre>
 * 1���������ж�̬bean���Ե���
 * 2���Զ�̬Bean����request����ĸ�ֵ����ȷ����̬Bean�����ݶ���ʵʱ��
 * </pre>
 * 
 * @author joe 2013-7-2 ����2:56:22
 */
public class PluginChecker {

    private static final Log    log      = LogFactory.getLog(PluginChecker.class);

    private static final String CHECKSTR = "checker://";

    public static void check(Object instance, Object proxy) {
        if (instance == null) {
            return;
        }
        String key = CHECKSTR + instance.getClass().getName() + "@" + Integer.toHexString(instance.hashCode());// instance.toString();

        // �Ƿ��ж�̬bean����
        Boolean isDynClass = ApplicationParamUtil.getContextParam(key);
        if (isDynClass != null && !isDynClass) {
            return;
        }
        // �Ƿ�ִ�й���̬����ע��
        Boolean checked = RequestParamUtil.getContextParam(key);
        if (checked != null && checked) {
            return;
        }

        // if (needLoadproxy(instance, proxy)) {
        // RequestParamUtil.addContextParam(key, true);
        // return;
        // }
        if (proxy == null) {
            return;
        }

        isDynClass = false;// �Ƿ�����˶�̬Bean���ԣ����������ÿ�ζ���Ҫע��
        // Field[] fields = obj.getClass().getDeclaredFields();
        Class<?> clazz = proxy.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                isDynClass = checkField(proxy, field);
                if (isDynClass) {
                    ApplicationParamUtil.addContextParam(key, isDynClass);
                }
            }
        }

        RequestParamUtil.addContextParam(key, true);
    }

    /**
     * ������������Ƿ���Ҫ���¼���
     * 
     * <pre>
     * �ж�������
     * 1���������Ϊ��
     * 2����ʵ���Ƕ�̬Bean
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
                    // DynamicBeanUtil.getProxy(pluginID, instance);
                    return true;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return false;
    }

    /**
     * ���ÿ������
     * 
     * @param obj
     * @param field
     * @param isDynClass
     */
    private static boolean checkField(Object obj, Field field) {
        field.setAccessible(true);
        String fieldName = field.getName();
        boolean isDynBeanField = PluginFactory.instance.getDynamicPluginIDs().contains(fieldName);
        if (!isDynBeanField) {
            return false;
        }

        // ����������
        Class<?> fieldClz = field.getType();
        if (isPrivateType(fieldClz)) {
            return false;
        }
        try {
            // ƥ��setXXX����������ҵ�����˵����Ҫע����
            Method method = obj.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), fieldClz);
            if (method == null) {
                return false;
            }
            // ֻ����̬���
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
        return true;
    }

    /**
     * �Ƿ�˽��ԭ������
     * 
     * @param clz
     * @return
     */
    public static boolean isPrivateType(Class<?> clz) {
        return clz == int.class || clz == short.class || clz == boolean.class || clz == long.class
               || clz == float.class || clz == double.class || clz == byte.class || clz.getName().startsWith("java.");
    }

}
