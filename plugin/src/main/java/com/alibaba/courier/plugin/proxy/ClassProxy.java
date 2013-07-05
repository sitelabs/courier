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
 * 代理类，由javassist提供字节码生成工具
 * 
 * <pre>
 * 1、为指定的class重新生成一个子类
 * 2、新的子类，重写public method，并增加proxy（原先的class实例）变量
 * 3、每个method的调用，都会增加check机制
 * 4、每个method的调用，都会代理给proxy类
 * </pre>
 * 
 * @author joe 2013年7月4日 下午9:59:31
 */
public class ClassProxy {

    private static final Log                   log              = LogFactory.getLog(ClassProxy.class);

    public static final ClassPool              cp               = ClassPool.getDefault();

    public static final String                 PROXY            = "proxy";
    private static final List<String>          classLoaderCache = Lists.newArrayList();               // 缓存已经添加过的classloader

    private static final Map<String, Class<?>> classCache       = Maps.newConcurrentMap();            // 缓存已经生成新的class对象

    /**
     * 添加依赖路径
     * 
     * @param cl
     */
    public static void addClassPath(ClassPath cl) {

        if (!classLoaderCache.contains(cl.toString())) {
            cp.appendClassPath(cl);
            classLoaderCache.add(cl.toString());
        }

    }

    /**
     * 创建新的静态代理类
     * 
     * @param clazz
     * @return
     */
    public static Class<?> create(Class<?> clazz) {

        String classNameKey = clazz.getName() + "$joe";// 新的类名
        if (classCache.containsKey(classNameKey)) {
            return classCache.get(classNameKey);
        }
        ClassLoader cl = clazz.getClassLoader();
        try {
            // 判断当前类库中是否可以超找到class，如果找不到，则添加该classload到仓库中
            cp.get(clazz.getName());
        } catch (Exception e) {
            addClassPath(new LoaderClassPath(cl));
        }
        // CtClass cc = cp.get("Hello");
        try {

            CtClass hc = cp.get(clazz.getName());
            // 创建新的class
            CtClass cc = cp.makeClass(classNameKey);
            cc.setSuperclass(hc);
            // 创建 proxy变量 ： 变量的类型为clazz
            CtField proxyField = new CtField(hc, PROXY, cc);
            proxyField.setModifiers(Modifier.PUBLIC);
            cc.addField(proxyField);

            List<String> methodCaches = Lists.newArrayList();
            // 遍历class的公共方法
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
                // 缓存同名的方法，避免当实现接口的时候，会出现重复method
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
