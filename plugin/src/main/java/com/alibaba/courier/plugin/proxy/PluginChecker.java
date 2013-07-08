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

    public static void check(Object obj) {
        if (obj == null) {
            return;
        }
        String key = CHECKSTR + obj.getClass();
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
        isDynClass = false;// �Ƿ�����˶�̬Bean���ԣ����������ÿ�ζ���Ҫע��
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            boolean isDynBeanField = PluginFactory.instance.getDynamicPluginIDs().contains(fieldName);
            if (isDynBeanField) {
                isDynClass = true;
            }

            // ����������
            Class<?> fieldClz = field.getType();
            if (isPrivateType(fieldClz)) {
                continue;
            }
            try {
                // ��������Ѿ���ֵ���򲻴���
                if (field.get(obj) != null && !isDynBeanField) {
                    continue;
                }
            } catch (Exception e1) {
                continue;
            }
            try {
                // ƥ��setXXX����������ҵ�����˵����Ҫע����
                Method method = obj.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), fieldClz);
                if (method == null) {
                    continue;
                }
                // �ж��Ƿ��Ƕ�̬bean������Ƕ�̬bean�����滻pluginId���������ҵ���ʵ�Ĳ��
                String pluginId = isDynBeanField ? fieldName + ClassProxy.PROXY : fieldName;
                // ֻ����̬���
                Object val = null;
                if (fieldClz.isAssignableFrom(List.class)) {
                    if (isDynBeanField) {
                        // ��̬bean������ʽִ�У����Դ���Ϊ�Ƿ�
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
