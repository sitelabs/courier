/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.courier.plugin.model;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Lists;

/**
 * Plugin Instance model
 * 
 * @author joe 2013-5-14 pm11:27:31
 */
public class PluginInstance {

    private int        index = 10;
    private Object     instance;
    private String     id;
    private String     scope;

    private Properties config;

    @SuppressWarnings("rawtypes")
    private Map        pluginConfig;

    private String[]   interfaceName;

    public PluginInstance(String id, Object instance){
        this.id = id;
        this.instance = instance;
    }

    @SuppressWarnings("rawtypes")
    public PluginInstance(String id, Integer index, Object instance){
        this.id = id;
        this.index = index;
        this.instance = instance;
        if (instance != null) {
            try {
                Class[] ifns = instance.getClass().getInterfaces();
                if (ifns != null) {
                    List<String> ls = Lists.newArrayList();
                    for (Class class1 : ifns) {
                        ls.add(class1.getName());
                    }
                    interfaceName = ls.toArray(new String[] {});
                }
            } catch (Exception e) {

            }
        }
        if (interfaceName == null) {
            interfaceName = new String[] { instance.getClass().getName() };
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("rawtypes")
    public Map getPluginConfig() {
        return pluginConfig;
    }

    @SuppressWarnings("rawtypes")
    public void setPluginConfig(Map pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public String[] getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String[] interfaceName) {
        this.interfaceName = interfaceName;
    }

}
