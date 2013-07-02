/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.china.courier.util.Utils.ApplicationParamUtil;
import com.google.common.collect.Maps;

/**
 * pulltool的工厂，用于模板当中访问util类等
 * 
 * @author joe 2013-6-30 下午7:47:54
 */
public class PullToolFactory {

    @SuppressWarnings("rawtypes")
    private Map                 pluginConfig;

    private Map<String, Object> cache;

    public static final String  URLS = "urls";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() {
        if (pluginConfig == null || pluginConfig.isEmpty()) {
            return;
        }
        cache = Maps.newConcurrentMap();

        Iterator it = pluginConfig.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) it.next();
            String pullToolName = entry.getKey();
            String pullToolClz = entry.getValue();
            initPulltool(pullToolName, pullToolClz);
        }

    }

    private void initPulltool(String pullToolName, String pullToolClz) {
        try {
            Object obj = PluginFactory.loadClass(pullToolClz).newInstance();
            cache.put(pullToolName, obj);
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
        }
    }

    /**
     * 手工添加pulltool
     * 
     * @param name
     * @param obj
     */
    public void add(String name, Object obj) {
        cache.put(name, obj);
    }

    public Object get(String name) {

        if (name.equals(URLS)) {
            return ApplicationParamUtil.getContextParam(URLS);
        }

        if (cache == null) {
            return null;
        }
        return cache.get(name);
    }

    public void setPluginConfig(Map pluginConfig) {
        this.pluginConfig = pluginConfig;
    }
}
