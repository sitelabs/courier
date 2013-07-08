/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.alibaba.courier.plugin.model.PluginInstance;
import com.alibaba.courier.plugin.proxy.ClassProxy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * plugin factory use for invoke plugin
 * 
 * @author joe 2013-5-13 pm3:43:03
 */
public class PluginFactory {

    protected static final Log                                log                 = LogFactory.getLog(PluginConfigurer.class);

    BundleContext                                             bundleContext;
    PluginConfigurer                                          _pc;

    public static PluginFactory                               instance;

    public static ConcurrentMap<String, List<PluginInstance>> pluginInstanceCache = Maps.newConcurrentMap();

    private List<String>                                      dynamicPluginIDs    = Lists.newArrayList();

    /**
     * 初始化插件容器，解析插件配置和完成类加载过程
     */
    public void initContainer(URL url) {
        if (this._pc == null) {
            PluginConfigurer pc = url == null ? PluginConfigurer.instance : new PluginConfigurer(url);
            this._pc = pc;
            _pc.setUrl(url);
            _pc.init(bundleContext, this);
        } else {
            _pc.setUrl(url);
            _pc.initPlugin();
        }
        String msg = "";
        if (bundleContext != null) {
            msg = bundleContext.getBundle().toString();
        }
        System.out.println(msg + " module startup success!");
    }

    /**
     * 初始化插件容器，解析插件配置和完成类加载过程
     */
    public void initContainer() {
        initContainer(null);
    }

    /**
     * 执行插件的AOP，依赖注入
     */
    public void initPluginIoc() {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        pc.initPluginIoc();
    }

    /**
     * 回调插件的init函数
     */
    public void initPlugin() {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        for (Map.Entry<String, List<PluginInstance>> entry : pc.plugins.entrySet()) {
            List<PluginInstance> instances = entry.getValue();
            for (PluginInstance pluginInstance : instances) {
                Method method = null;
                try {
                    method = pluginInstance.getInstance().getClass().getMethod("init", null);
                } catch (NoSuchMethodException e) {
                    continue;
                }
                try {
                    if (method != null) {
                        Field field = pluginInstance.getInstance().getClass().getDeclaredField(ClassProxy.PROXY);
                        if (field != null) {
                            Object proxy = field.get(pluginInstance.getInstance());
                            proxy.getClass().getMethod("init", null).invoke(proxy, null);
                        } else {
                            method.invoke(pluginInstance.getInstance(), null);
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    public Set<String> getPluginIDs() {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        return pc.plugins.keySet();
    }

    /**
     * 获取动态插件的id集合
     * 
     * @return
     */
    public List<String> getDynamicPluginIDs() {
        return dynamicPluginIDs;
    }

    protected List<PluginInstance> getSimplePlugininstances(String pluginID) {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        return pc.plugins.get(pluginID);
    }

    @SuppressWarnings("unchecked")
    private List<PluginInstance> getPlugininstances(String pluginID) {

        if (pluginInstanceCache.containsKey(pluginID)) {
            return pluginInstanceCache.get(pluginID);
        }
        List<String> plugnInstanceClzs = Lists.newArrayList();// 用来防止同一个plugin被重复添加
        List<PluginInstance> plugins = getSimplePlugininstances(pluginID);

        if (plugins == null) {
            plugins = Lists.newArrayList();
        } else {
            for (PluginInstance pluginInstance : plugins) {
                String name = pluginInstance.getInstance().toString();
                if (!plugnInstanceClzs.contains(name)) {
                    plugnInstanceClzs.add(name);
                }
            }
        }
        // 开销巨大
        if (bundleContext != null) {
            try {
                ServiceReference[] scr = bundleContext.getAllServiceReferences(PluginFactory.class.getName(), null);
                for (ServiceReference serviceReference : scr) {
                    try {
                        PluginFactory pluginFactory = (PluginFactory) bundleContext.getService(serviceReference);
                        List<PluginInstance> bundlePlugins = pluginFactory.getSimplePlugininstances(pluginID);
                        if (bundlePlugins != null) {
                            for (PluginInstance pluginInstance : bundlePlugins) {
                                String clzName = pluginInstance.getInstance().toString();
                                if (!plugnInstanceClzs.contains(clzName)) {
                                    plugins.add(pluginInstance);
                                    plugnInstanceClzs.add(clzName);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        if (!plugins.isEmpty()) {
            sortPlugins(plugins);
        }

        pluginInstanceCache.put(pluginID, plugins);

        return plugins;
    }

    /**
     * sort the pluginInstance order by index asc
     * 
     * @param plugins
     */
    protected static void sortPlugins(List<PluginInstance> plugins) {
        Collections.sort(plugins, new Comparator<PluginInstance>() {

            public int compare(PluginInstance mapping1, PluginInstance mapping2) {
                return mapping1.getIndex() < mapping2.getIndex() ? -1 : (mapping1.getIndex() == mapping2.getIndex() ? 0 : 1);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getPlugin(String pluginID) {
        List<PluginInstance> plugins = getPlugininstances(pluginID);
        if (plugins != null && !plugins.isEmpty()) {
            return (T) plugins.get(0).getInstance();
        }
        return null;
    }

    public <T> List<T> getPlugins(String pluginID) {

        List<PluginInstance> plugins = getPlugininstances(pluginID);

        if (plugins == null) {
            return null;
        }
        List<T> objs = Lists.newArrayList();
        for (PluginInstance p : plugins) {
            objs.add((T) p.getInstance());
        }
        return objs;

    }

    /**
     * 提供注册机制，让各种对象可以轻松的注册到容器中
     * 
     * @param pluginId
     * @param className
     * @param instance
     */
    public void register(String pluginId, String className, Object instance) {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        PluginInstance ins = new PluginInstance(pluginId, instance);

        pc.plugins.put(pluginId, Lists.newArrayList(ins));
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public static Class<?> loadClass(String clz) throws ClassNotFoundException {
        if (instance != null) {
            try {
                return instance.getBundleContext().getBundle().loadClass(clz);
            } catch (ClassNotFoundException e) {
            }
        }
        try {
            return Class.forName(clz);
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

}
