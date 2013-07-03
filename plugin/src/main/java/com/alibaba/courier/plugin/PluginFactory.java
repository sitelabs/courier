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
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.alibaba.china.courier.util.ReflectUtils;
import com.alibaba.china.courier.util.Utils.RequestParamUtil;
import com.alibaba.courier.plugin.asm.ASMClassUtil;
import com.alibaba.courier.plugin.model.PluginInstance;
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

    public void initContainer(URL url) {
        if (this._pc == null) {
            PluginConfigurer pc = url == null ? PluginConfigurer.instance : new PluginConfigurer(url);
            this._pc = pc;
        }
        _pc.setUrl(url);
        _pc.init(bundleContext, this);

        String msg = "";
        if (bundleContext != null) {
            msg = bundleContext.getBundle().toString();
        }
        System.out.println(msg + " module startup success!");
    }

    public void initContainer() {
        initContainer(null);
    }

    public void initPluginIoc() {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        pc.initPluginIoc();
    }

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
                        method.invoke(pluginInstance.getInstance(), null);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * 得到所有的插件集合
     * 
     * @return
     */
    public List<String> getPluginIDs() {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        return Lists.newArrayList(pc.plugins.keySet().iterator());
    }

    /**
     * 得到动态插件的id集合
     * 
     * @return
     */
    public List<String> getDynamicPluginIDs() {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        return pc.dynPluginids;
    }

    /**
     * 从当前jar的插件容器进行查找插件
     * 
     * @param pluginID
     * @return
     */
    protected List<PluginInstance> getSimplePlugininstances(String pluginID) {
        PluginConfigurer pc = _pc == null ? PluginConfigurer.instance : _pc;
        return pc.plugins.get(pluginID);
    }

    /**
     * 从所有的插件容器中检索插件
     * 
     * @param pluginID
     * @return
     */
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
                plugnInstanceClzs.add(pluginInstance.getInstance().getClass().getName());
            }
        }
        // 开销巨大，从osgi插件容器中查找
        if (bundleContext != null) {
            try {
                ServiceReference[] scr = bundleContext.getAllServiceReferences(PluginFactory.class.getName(), null);
                for (ServiceReference serviceReference : scr) {
                    PluginFactory pluginFactory = (PluginFactory) bundleContext.getService(serviceReference);
                    List<PluginInstance> bundlePlugins = pluginFactory.getSimplePlugininstances(pluginID);
                    if (bundlePlugins != null) {
                        for (PluginInstance pluginInstance : bundlePlugins) {
                            String clzName = pluginInstance.getInstance().getClass().getName();
                            if (!plugnInstanceClzs.contains(clzName)) {
                                plugins.add(pluginInstance);
                            }
                        }

                    }
                }
            } catch (InvalidSyntaxException e) {
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
                return mapping1.getIndex().compareTo(mapping2.getIndex());
            }
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T getPlugin(String pluginID) {
        List<PluginInstance> plugins = getPlugininstances(pluginID);
        if (plugins != null && !plugins.isEmpty()) {
            Object obj = plugins.get(0).getInstance();
            if (obj instanceof DynamicBean) {
                String key = pluginID + "_loader";
                T t = RequestParamUtil.getContextParam(key);
                if (t != null) {
                    return t;
                }

                try {
                    T loader = (T) ((DynamicBean) obj).load();
                    // 创建动态类
                    Class<T> clz = (Class<T>) ASMClassUtil.getEnhancedClass(loader.getClass());
                    t = clz.newInstance();

                    // 将load对象的属性值拷贝给动态类
                    setInstanceField(t, loader, clz);

                    return loader;
                } catch (Exception e) {
                    log.error(" invoke the " + obj.getClass() + ".load error", e);
                    return null;
                }
            }
            return (T) obj;
        }
        return null;
    }

    /**
     * @param t
     * @param loader
     * @param clz
     * @throws IllegalAccessException
     */
    public static <T> void setInstanceField(T target, T source, Class<T> clz) throws IllegalAccessException {
        for (Field field : clz.getDeclaredFields()) {
            field.setAccessible(true);
            Object val = ReflectUtils.get(source, field);
            field.set(target, val);
        }
        for (Field field : clz.getSuperclass().getDeclaredFields()) {
            field.setAccessible(true);
            Object val = ReflectUtils.get(source, field);
            field.set(target, val);
        }
    }

    /**
     * 获取插件集合
     * 
     * @param pluginID
     * @return
     */
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
     * 向容器注册制定的插件
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

    /**
     * 将对象放在插件容器中进行包装，主要进行依赖注入
     * 
     * @param object
     */
    public void warp(Object object) {
        if (object != null) {
            PluginInstance instance = new PluginInstance(object.getClass().getName(), object);

            try {
                _pc.pluginIoc(instance);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    /**
     * 自动加载class类
     * 
     * @param clz
     * @return
     * @throws ClassNotFoundException
     */
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
