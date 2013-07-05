/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.proxy;

import java.util.List;
import java.util.Map;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.china.courier.util.Utils.GolbalConstants;
import com.alibaba.courier.plugin.PluginFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 类ClassUtil.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年7月4日 下午9:59:31
 */
public class ClassProxy {

    private static final Log          log              = LogFactory.getLog(ClassProxy.class);

    public static final ClassPool     cp               = ClassPool.getDefault();

    public static final String        PROXY            = "proxy";
    private static final List<String> classLoaderCache = Lists.newArrayList();

    private static final Map<String, Class> classCache       = Maps.newConcurrentMap();


    public static void addClassPath(ClassPath cl) {

        if (!classLoaderCache.contains(cl.toString())) {
            cp.appendClassPath(cl);
            classLoaderCache.add(cl.toString());
        }

    }

    public static Class<?> create(Class<?> clazz) {

        String classNameKey = clazz.getName() + "$joe";
        if (classCache.containsKey(classNameKey)) {
            return classCache.get(classNameKey);
        }
        ClassLoader cl = clazz.getClassLoader();
        try {
            cp.get(clazz.getName());
        } catch (Exception e) {
            addClassPath(new LoaderClassPath(cl));
        }
        // CtClass cc = cp.get("Hello");
        try {

            CtClass hc = cp.get(clazz.getName());

            CtClass cc = cp.makeClass(classNameKey);
            cc.setSuperclass(hc);
            CtField proxyField = new CtField(hc, PROXY, cc);
            proxyField.setModifiers(Modifier.PUBLIC);
            cc.addField(proxyField);

            List<String> methodCaches = Lists.newArrayList();

            for (CtMethod method : hc.getDeclaredMethods()) {

                if (method.getModifiers() != Modifier.PUBLIC) {
                    continue;
                }
                CtMethod md = null;
                try {
                    md = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), cc);
                } catch (javassist.NotFoundException e) {
                    Class clz = PluginFactory.loadClass(e.getMessage());
                    addClassPath(new LoaderClassPath(clz.getClassLoader()));
                    md = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), cc);
                }

                if (method.getName().equals("init")) {
                    continue;
                }

                String key = method.getName();
                for (CtClass ctClass : method.getParameterTypes()) {
                    key += ctClass.getName();
                }
                if (methodCaches.contains(key)) {
                    continue;
                }
                methodCaches.add(key);

                String returnTypeName = method.getReturnType().getName();
                String returnStr = "return ";
                boolean isVoid = false;// 返回值是否是void
                if (returnTypeName.equals("void")) {
                    returnStr = StringUtils.EMPTY;
                    isVoid = true;
                }
                List<String> params = Lists.newArrayList();
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    params.add("$" + (i + 1));
                }
                String paramStr = StringUtils.join(params, GolbalConstants.ARRAY_SPLIT);
                if (paramStr == null) {
                    paramStr = StringUtils.EMPTY;
                }

                String checkStr = "com.alibaba.courier.plugin.proxy.PluginChecker.check(proxy);";
                // 判断是否是set方法，如果是就进行check
                if (isVoid && method.getName().startsWith("set") && params.size() == 1) {
                    checkStr = StringUtils.EMPTY;
                }

                md.setBody("{ " + checkStr + returnStr + " proxy." + method.getName() + "(" + paramStr + ");}");
                cc.addMethod(md);
            }
            Class rclazz = cc.toClass(cl);
            classCache.put(classNameKey, rclazz);
            return rclazz;

        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static String toClassName(String clazz) {
        return GolbalConstants.PATH_SPLIT + clazz.replace('.', '/');
    }

}
