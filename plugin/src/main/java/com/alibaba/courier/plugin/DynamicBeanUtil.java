/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.proxy.PluginChecker;

/**
 * 动态类的辅助工具类
 * 
 * @author joe 2013年7月7日 下午9:46:56
 */
public class DynamicBeanUtil {

    /**
     * 加载动态Bean
     * 
     * @param bean
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object load(DynamicBean bean) {
        String key = bean.getClass().getName() + ".load";
        Object result = RequestParamUtil.getContextParam(key);
        if (result != null) {
            return result;
        }
        result = bean.load();
        RequestParamUtil.addContextParam(key, result);
        PluginChecker.check(result);
        return result;
    }

}
