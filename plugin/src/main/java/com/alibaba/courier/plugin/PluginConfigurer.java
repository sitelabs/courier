/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

import com.alibaba.china.courier.util.ObjectInvoker;
import com.alibaba.china.courier.util.Utils.GolbalConstants;
import com.alibaba.courier.plugin.annotation.Plugin;
import com.alibaba.courier.plugin.model.PluginInstance;
import com.alibaba.courier.plugin.model.xml.PluginAppType;
import com.alibaba.courier.plugin.model.xml.PluginType;
import com.alibaba.courier.plugin.model.xml.PropertiesType;
import com.alibaba.courier.plugin.model.xml.PropertyType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * plugin config manager
 * 
 * @author joe 2013-5-13 pm4:13:06
 */
public class PluginConfigurer {

    protected static final Log                            log                 = LogFactory.getLog(PluginConfigurer.class);
    public static final String                            PLUGIN_FILE         = "plugin.xml";

    public static final String                            PLUGIN_CONFIG_FILE  = "plugin.properties";

    PluginFactory                                         _pluginFactory;

    public static PluginConfigurer                        instance;

    static Unmarshaller                                   m;

    private URL                                           url;

    static {
        instance = new PluginConfigurer();

    }
    protected ConcurrentMap<String, List<PluginInstance>> plugins             = Maps.newConcurrentMap();

    protected ConcurrentMap<String, Properties>           pluginConfigs       = Maps.newConcurrentMap();

    protected Properties                                  commonPluginConfigs = new Properties();

    private PluginConfigurer(){
    }

    public PluginConfigurer(URL url){
        this.url = url;
    }

    public void init(BundleContext bundleContext, PluginFactory pluginFactory) {

        _pluginFactory = pluginFactory;

        List<PluginType> pts = scan();
        if (pts.isEmpty()) {
            return;
        }
        Map<String, List<Integer>> pluginIndexs = Maps.newHashMap();
        for (PluginType pluginType : pts) {
            parsePlugin(pluginType, pluginIndexs);
        }

        if (bundleContext != null && this.url != null) {
            bundleContext.registerService(PluginFactory.class.getName(), pluginFactory, null);
        }

    }

    /**
     * each plugins for plugin ioc
     */
    public void initPluginIoc() {

        for (Map.Entry<String, List<PluginInstance>> entry : plugins.entrySet()) {
            List<PluginInstance> instances = entry.getValue();
            for (PluginInstance pluginInstance : instances) {
                try {
                    pluginIoc(pluginInstance);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * 执行依赖注入，支持两种：
     * 
     * <pre>
     * 1、注解方式
     * 2、set方式
     * </pre>
     * 
     * @param pluginInstance
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void pluginIoc(PluginInstance pluginInstance) throws IllegalArgumentException, IllegalAccessException {
        Object obj = pluginInstance.getInstance();

        Class objCls = obj.getClass();

        Field[] fs = objCls.getDeclaredFields();

        Map config = getPluginConfig(pluginInstance);

        for (Field field : fs) {
            field.setAccessible(true);
            Plugin pluginAnno = field.getAnnotation(Plugin.class);
            boolean isArrayField = field.getType().isAssignableFrom(List.class);

            if (pluginAnno != null) {
                String pluginID = pluginAnno.id();
                StrSubstitutor sub = new StrSubstitutor(config);
                pluginID = sub.replace(pluginID);
                setField(obj, field, isArrayField, pluginID);
                continue;
            }
            if (StringUtils.equals(field.getName(), "pluginConfig") && field.getType().equals(Map.class)) {
                // init pluginConfig
                field.set(obj, config);
                continue;
            }
            Method method = null;
            try {
                method = objCls.getMethod("set" + StringUtils.capitalize(field.getName()), field.getType());
                if (method != null) {
                    Object refPlugin = getRealPlugin(isArrayField, field.getName());
                    if (refPlugin != null) {
                        method.invoke(obj, refPlugin);
                    }
                }
                // ignor exception
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            } catch (Exception e) {
                log.error("invoke " + obj.getClass() + "." + method.getName(), e);
            }

        }
    }

    /**
     * @param obj
     * @param field
     * @param isArrayField
     * @param pluginID
     * @throws IllegalAccessException
     */
    private void setField(Object obj, Field field, boolean isArrayField, String pluginID) throws IllegalAccessException {
        Object refPlugin = null;
        refPlugin = getRealPlugin(isArrayField, pluginID);

        if (refPlugin != null) {
            if (log.isDebugEnabled()) {
                log.debug("load plugin:" + pluginID + " success");
            }
            field.set(obj, refPlugin);
        }
    }

    /**
     * @param isArrayField
     * @param pluginID
     * @return
     */
    private Object getRealPlugin(boolean isArrayField, String pluginID) {
        Object refPlugin;
        if (isArrayField) {
            refPlugin = _pluginFactory.getPlugins(pluginID);
        } else {
            refPlugin = _pluginFactory.getPlugin(pluginID);
        }

        if (refPlugin == null && "pluginFactory".equals(pluginID)) {
            refPlugin = _pluginFactory;
        }

        if (refPlugin != null && refPlugin instanceof DynamicBean) {
            try {
                Object loader = ObjectInvoker.handler(pluginID, refPlugin, "load");
                PluginFactory.instance.warp(loader);
                return loader;
            } catch (Exception e) {
                log.error("", e);
            }
            return null;
        }

        return refPlugin;
    }

    /**
     * merge common config ,plugin config
     * 
     * @param pluginInstance
     * @return
     */
    private Map getPluginConfig(PluginInstance pluginInstance) {
        Map config = Maps.newHashMap();
        for (Map.Entry entry : commonPluginConfigs.entrySet()) {
            config.put(entry.getKey(), entry.getValue());
        }
        Properties pro = pluginConfigs.get(pluginInstance.getId());
        if (pro != null) {
            for (Map.Entry entry : pro.entrySet()) {
                config.put(entry.getKey(), entry.getValue());
            }
        }
        pro = pluginInstance.getConfig();
        if (pro != null) {
            for (Map.Entry entry : pro.entrySet()) {
                config.put(entry.getKey(), entry.getValue());
            }
        }

        pluginInstance.setPluginConfig(config);
        return config;
    }

    /**
     * parse plugin
     * 
     * <pre>
     *   parse plugin instance config
     * </pre>
     * 
     * @param pluginType
     * @param pluginIndexs Map<String, List<Integer>> use for
     */
    private void parsePlugin(PluginType pluginType, Map<String, List<Integer>> pluginIndexs) {

        if (StringUtils.isEmpty(pluginType.getClazz())) {
            return;
        }
        String pluginID = pluginType.getId();
        String pluginClassName = pluginType.getClazz();
        // create the plugin instance
        Object pluginInstance = null;
        try {
            pluginInstance = newPluginInstance(pluginClassName);
        } catch (Exception e) {
            log.error("init plugin:" + pluginID + " in " + pluginClassName + " error:", e);
            return;
        }

        List<Integer> indexs = pluginIndexs.get(pluginID);
        if (indexs == null) {
            indexs = new ArrayList<Integer>();
            pluginIndexs.put(pluginID, indexs);
        } else {
            // use the index value be sure no reduplicate plugin instance
            if (indexs.contains(pluginType.getIndex())) {
                return;
            }
        }
        indexs.add(pluginType.getIndex());

        parsePluginInstance(pluginType, pluginInstance);

    }

    /**
     * parse PluginInstance impl
     * 
     * @param pluginID
     * @param pluginIndex
     * @param pluginInstance
     * @param indexs
     */
    private void parsePluginInstance(PluginType pluginType, Object pluginInstance) {
        PluginInstance instance = new PluginInstance(pluginType.getId(), pluginType.getIndex(), pluginInstance);
        instance.setScope(pluginType.getScope());

        PropertiesType propertiesType = pluginType.getProperties();

        if (propertiesType != null) {
            List<PropertyType> propertyTypes = propertiesType.getProperty();
            Properties prot = new Properties();
            instance.setConfig(prot);
            for (PropertyType pt : propertyTypes) {
                try {
                    prot.put(pt.getKey(), pt.getValue());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        // fill plugin instance array
        List<PluginInstance> plugins = this.plugins.get(pluginType.getId());
        if (plugins == null) {
            plugins = new ArrayList<PluginInstance>();
            this.plugins.put(pluginType.getId(), plugins);
        }
        plugins.add(instance);

    }

    /**
     * instantiation the Plugin class
     * 
     * @param pluginClassName
     * @return
     * @throws Exception
     */
    public Object newPluginInstance(String pluginClassName) throws Exception {
        Class<?> cls = null;
        if (_pluginFactory.bundleContext != null) {
            cls = _pluginFactory.bundleContext.getBundle().loadClass(pluginClassName);
        } else {
            cls = Class.forName(pluginClassName);
        }
        Object obj = cls.newInstance();
        return obj;
    }

    /**
     * scan plugin.properties
     */
    @SuppressWarnings("rawtypes")
    private List<PluginType> scan() {
        if (m == null) {
            try {
                ClassLoader cl = com.alibaba.courier.plugin.model.xml.ObjectFactory.class.getClassLoader();
                JAXBContext jc = JAXBContext.newInstance("com.alibaba.courier.plugin.model.xml", cl);
                m = jc.createUnmarshaller();
            } catch (JAXBException e) {
                log.error("", e);
            }
        }
        if (m == null) {
            return Collections.EMPTY_LIST;
        }
        List<PluginType> pts = Lists.newArrayList();
        try {
            if (url != null) {
                InputStream in = url.openStream();
                parseXML(pts, in);
            } else {
                Enumeration<URL> urls = PluginConfigurer.class.getClassLoader().getResources(PLUGIN_FILE);
                while (urls != null && urls.hasMoreElements()) {
                    InputStream in = urls.nextElement().openStream();
                    parseXML(pts, in);
                }
            }
        } catch (IOException e) {
        }
        scanPluginConfig();

        return pts;
    }

    /**
     * @param pts
     * @param in
     * @throws JAXBException
     * @throws IOException
     */
    private void parseXML(List<PluginType> pts, InputStream in) {
        try {
            JAXBElement element = (JAXBElement) m.unmarshal(in);
            PluginAppType plugins = (PluginAppType) element.getValue();
            pts.addAll(plugins.getPlugin());
            in.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * scan and parse plugin.properties. split config for common config or pluginConfig
     */
    private void scanPluginConfig() {
        Properties pro = new Properties();
        try {
            if (_pluginFactory.bundleContext != null) {
                URL url = _pluginFactory.bundleContext.getBundle().getResource(PLUGIN_CONFIG_FILE);
                if (url != null) {
                    InputStream in = url.openStream();
                    try {
                        pro.load(in);
                    } catch (Exception e) {
                    }
                    in.close();
                }
            } else {
                Enumeration<URL> urls = PluginConfigurer.class.getClassLoader().getResources(PLUGIN_CONFIG_FILE);
                while (urls != null && urls.hasMoreElements()) {
                    InputStream in = urls.nextElement().openStream();
                    try {
                        pro.load(in);
                    } catch (Exception e) {
                    }
                    in.close();
                }
            }
        } catch (IOException e) {
        }
        Enumeration<?> keys = pro.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Object val = pro.get(key);
            String configKey = key;
            int splitIndex = key.indexOf(GolbalConstants.PACKAGE_SPLIT);
            if (splitIndex != -1) {
                String pluginId = key.substring(0, splitIndex);
                configKey = key.substring(splitIndex + 1);

                Properties configPro = pluginConfigs.get(pluginId);
                if (configPro == null) {
                    configPro = new Properties();
                    pluginConfigs.put(pluginId, configPro);
                }
                configPro.put(configKey, val);
            } else {
                commonPluginConfigs.put(key, val);
            }
        }
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // instance.scanProperties();

        String val = "1:test";

        int index = val.indexOf(GolbalConstants.URI_SPLIT);
        if (index != -1) {
            int pluginIndex = Integer.parseInt(val.substring(0, index));
            System.out.println(pluginIndex);
            System.out.println(val.substring(index + 1));
        }
    }

}
