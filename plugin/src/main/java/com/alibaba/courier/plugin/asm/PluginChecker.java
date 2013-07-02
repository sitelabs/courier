/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.asm;

import java.util.List;

import com.alibaba.courier.plugin.DynamicBean;
import com.alibaba.courier.plugin.PluginFactory;

/**
 * 插件校验器
 * 
 * @author joe 2013-7-2 下午2:56:22
 */
public class PluginChecker {

    public static void check(Object obj) {

        if (obj instanceof DynamicBean) {

        }

    }

    /**
     * @param args
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String pluginID) {

        T t = null;
        if (t instanceof List) {
            return (T) PluginFactory.instance.getPlugins(pluginID);
        }
        return PluginFactory.instance.getPlugin(pluginID);
    }

}
