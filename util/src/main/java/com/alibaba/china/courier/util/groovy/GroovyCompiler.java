/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.util.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.alibaba.china.courier.model.Resource;
import com.alibaba.china.courier.util.Utils.RequestParamUtil;

/**
 * ��GroovyCompile.java��ʵ�����������벢����Groovy
 * 
 * @author joe 2012-4-30 ����4:39:31
 */
public class GroovyCompiler {

    private static final Log                log               = LogFactory.getLog(GroovyCompiler.class);

    private static GroovyClassLoader        gcl               = new GroovyClassLoader(
                                                                                      Thread.currentThread().getContextClassLoader());

    // ����
    @SuppressWarnings("rawtypes")
    private static Map<String, GroovyClass> groovyCaches      = new ConcurrentHashMap<String, GroovyClass>();

    private static Map<String, ClassCache>  groovyClassCaches = new ConcurrentHashMap<String, ClassCache>();

    /**
     * �����ļ�
     * 
     * @param res
     */
    public static void compiler(String name, String source) {
        try {
            gcl.parseClass(source, name);
        } catch (Exception e) {
            log.error("", e);
        }

    }

    /**
     * ȡ��grovvy bean�ı�������
     * 
     * @param res
     * @return
     */
    public static GroovyClass<?> getClass(Resource res) {
        GroovyClass<GroovyObject> object = getCache(res);
        if (object != null) {
            return object;
        }
        synchronized (res) {
            try {
                if (res.getAsString() != null) {
                    String name = res.getName();
                    String sourceCode = res.getAsString();
                    Class<?> scriptClass = gcl.parseClass(sourceCode, name);

                    if (!scriptClass.isInterface()) {
                        GroovyObject script = (GroovyObject) scriptClass.newInstance();
                        object = new GroovyClass<GroovyObject>(script);
                        object.setLastmodified(res.getLastModified());
                        groovyCaches.put(name, object);
                    }
                    return object;
                }

            } catch (Exception e) {
                log.error("compile +" + res.getName() + " error:", e);
            }
        }

        return null;
    }

    /**
     * ���벢�õ�groovy�ű�����
     * 
     * @param res
     * @return
     */
    public static GroovyClass<Script> getScript(Resource res) {

        // ��ʼ�������Ĳ���
        Map<String, Object> context = new HashMap<String, Object>();
        // ��ʼ�����������Ĳ���
        context.putAll(RequestParamUtil.getContextParams());

        Binding binding = new Binding(context);

        ClassCache cls = getCacheCls(res);
        if (cls != null) {
            Script script = InvokerHelper.createScript(cls.getCls(), binding);
            return new GroovyClass<Script>(script);
        }
        try {
            String sourceCode = res.getAsString();
            if (sourceCode != null) {
                String name = res.getName();
                Class<?> scriptClass = gcl.parseClass(sourceCode, name);
                groovyClassCaches.put(name, new ClassCache(scriptClass, res.getLastModified()));
                Script script = InvokerHelper.createScript(scriptClass, binding);
                return new GroovyClass<Script>(script);

            }
        } catch (Exception e) {
            log.error("compile +" + res.getName() + " error:", e);
        }

        return null;
    }

    /**
     * �ӻ����л�ȡ�����Ķ���
     * 
     * @param res
     */
    private static ClassCache getCacheCls(Resource res) {
        if (groovyClassCaches.containsKey(res.getName())) {
            ClassCache cls = groovyClassCaches.get(res.getName());
            if (cls != null && cls.getLastmodified() == res.getLastModified()) {
                return cls;
            }
        }
        return null;
    }

    /**
     * �ӻ����л�ȡ�����Ķ���
     * 
     * @param res
     */
    private static <T> GroovyClass<T> getCache(Resource res) {
        if (groovyCaches.containsKey(res.getName())) {
            GroovyClass object = groovyCaches.get(res.getName());
            if (object != null && object.getLastmodified() == res.getLastModified()) {
                return object;
            }
        }
        return null;
    }

    public static class ClassCache {

        private long  lastmodified = 0;
        private Class cls;

        public ClassCache(Class cls, long lastmodified){
            this.cls = cls;
            this.lastmodified = lastmodified;
        }

        public long getLastmodified() {
            return lastmodified;
        }

        public void setLastmodified(long lastmodified) {
            this.lastmodified = lastmodified;
        }

        public Class getCls() {
            return cls;
        }

        public void setCls(Class cls) {
            this.cls = cls;
        }

    }

}
