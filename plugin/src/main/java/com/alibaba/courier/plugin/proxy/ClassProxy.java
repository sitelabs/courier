/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.china.courier.util.Utils.GolbalConstants;
import com.alibaba.courier.plugin.PluginFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * �����࣬��javassist�ṩ�ֽ������ɹ���
 * 
 * <pre>
 * 1��Ϊָ����class��������һ������
 * 2���µ����࣬��дpublic method��������proxy��ԭ�ȵ�classʵ��������
 * 3��ÿ��method�ĵ��ã���������check����
 * 4��ÿ��method�ĵ��ã���������proxy��
 * </pre>
 * 
 * @author joe 2013��7��4�� ����9:59:31
 */
public class ClassProxy {

    private static final Log                   log               = LogFactory.getLog(ClassProxy.class);

    public static final ClassPool              cp                = ClassPool.getDefault();

    public static final String                 PROXY             = "proxy";
    public static final String                 PLUGINID          = "plugin_id";
    private static final List<String>          classLoaderCache  = Lists.newArrayList();               // �����Ѿ���ӹ���classloader

    private static final Map<String, Class<?>> classCache        = Maps.newConcurrentMap();            // �����Ѿ������µ�class����

    public static List<String>                 objectMethodcache = Lists.newArrayList();               // ����
                                                                                                        // Object.class��ԭ��public��������

    private static Object                      locked            = new Object();

    static {

        for (Method method : Object.class.getMethods()) {
            objectMethodcache.add(getMethodDesc(method));
        }
    }

    private static String getMethodDesc(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());
        if (method.getParameterTypes() != null) {
            for (Class<?> clzz : method.getParameterTypes()) {
                sb.append(clzz.getName());
            }
        }
        return sb.toString();
    }

    /**
     * �������·��
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
     * �����µľ�̬������
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <C> Class<C> create(Class<?> clazz, String pluginId, boolean isPlugin) {

        String classNameKey = clazz.getName() + "$joe";// �µ�����
        if (classCache.containsKey(classNameKey)) {
            return (Class<C>) classCache.get(classNameKey);
        }

        synchronized (locked) {
            // ˫��������֤��������������ظ�������
            if (classCache.containsKey(classNameKey)) {
                return (Class<C>) classCache.get(classNameKey);
            }
            return createNewClass(clazz, pluginId, isPlugin, classNameKey);
        }

    }

    /**
     * @param clazz
     * @param pluginId
     * @param isPlugin
     * @param classNameKey
     */
    @SuppressWarnings("rawtypes")
    private static Class createNewClass(Class<?> clazz, String pluginId, boolean isPlugin, String classNameKey) {
        ClassLoader cl = clazz.getClassLoader();
        try {
            // �жϵ�ǰ������Ƿ���Գ��ҵ�class������Ҳ���������Ӹ�classload���ֿ���
            cp.get(clazz.getName());
        } catch (Exception e) {
            addClassPath(new LoaderClassPath(cl));
        }
        try {
            CtClass superClazz = cp.get(clazz.getName());
            // �����µ�class
            CtClass newClazz = cp.makeClass(classNameKey);
            newClazz.setSuperclass(superClazz);
            // ���� proxy���� �� ����������Ϊclazz
            CtField proxyField = new CtField(superClazz, PROXY, newClazz);
            proxyField.setModifiers(Modifier.PUBLIC);
            newClazz.addField(proxyField);

            CtField pluginField = new CtField(cp.get(String.class.getName()), PLUGINID, newClazz);
            pluginField.setModifiers(Modifier.PUBLIC);
            newClazz.addField(pluginField);

            initConstructor(superClazz, newClazz);

            List<String> methodCaches = Lists.newArrayList();
            // ����class�Ĺ�������
            for (; clazz != Object.class && !clazz.isInterface(); clazz = clazz.getSuperclass()) {
                CtClass _superClazz = cp.get(clazz.getName());
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getModifiers() != 1 && method.getModifiers() != 9) {
                        continue;
                    }
                    String methodDesc = getMethodDesc(method);
                    if (objectMethodcache.contains(methodDesc)) {
                        continue;
                    }

                    if (method.getName().equals("init")) {
                        continue;
                    }
                    String key = method.getName();
                    for (Class<?> ctClass : method.getParameterTypes()) {
                        key += ctClass.getName();
                    }
                    if (methodCaches.contains(key)) {
                        continue;
                    }
                    // ����ͬ���ķ��������⵱ʵ�ֽӿڵ�ʱ�򣬻�����ظ�method
                    methodCaches.add(key);

                    CtMethod ctd = getMethod(_superClazz, method);

                    CtMethod md = createCtMethod(newClazz, ctd);

                    String returnTypeName = method.getReturnType().getName();

                    boolean isVoid = false;// ����ֵ�Ƿ���void
                    if (returnTypeName.equals("void")) {
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

                    String checkStr = "com.alibaba.courier.plugin.proxy.PluginChecker.check(this,proxy);";
                    // �ж��Ƿ���set����������Ǿͽ���check
                    if (isVoid && method.getName().startsWith("set") && params.size() == 1) {
                        checkStr = StringUtils.EMPTY;
                    }
                    StringBuilder body = new StringBuilder();
                    body.append("{ ");
                    // body.append(checkStr);

                    String invokeString = method.getName() + "(" + paramStr + ");";
                    body.append(clazz.getName() + "  _proxy = proxy; ");
                    if (!isPlugin) {
                        body.append("_proxy=  (" + clazz.getName()
                                    + ")com.alibaba.courier.plugin.DynamicBeanUtil.getProxy(\"" + pluginId + "\");");
                    }
                    if (isVoid) {
                        body.append("if(_proxy!=null){");
                        body.append("_proxy.").append(invokeString);
                        body.append("}else{ try{");
                        body.append("super.");
                    } else {
                        body.append("if(_proxy!=null){");
                        body.append("return _proxy.").append(invokeString);
                        body.append("}else{ try{");
                        body.append("return super.");
                    }
                    body.append(invokeString).append("}catch (Exception e) {return ");
                    body.append(getNullVal(method.getReturnType())).append(";}}");
                    body.append("}");
                    if (log.isDebugEnabled()) {
                        String msg = "the class " + clazz.getName() + "." + method.getName() + " source is: " + body;
                        // System.out.println(msg);
                        log.debug(msg);
                    }
                    md.setBody(body.toString());
                    newClazz.addMethod(md);
                }
            }
            Class rclazz = newClazz.toClass(cl);
            classCache.put(classNameKey, rclazz);
            return rclazz;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private static CtMethod createCtMethod(CtClass newClazz, CtMethod ctd) throws ClassNotFoundException,
                                                                          NotFoundException {
        CtMethod md;
        try {
            md = new CtMethod(ctd.getReturnType(), ctd.getName(), ctd.getParameterTypes(), newClazz);
        } catch (javassist.NotFoundException e) {
            Class<?> clz = PluginFactory.loadClass(e.getMessage());
            addClassPath(new LoaderClassPath(clz.getClassLoader()));
            md = new CtMethod(ctd.getReturnType(), ctd.getName(), ctd.getParameterTypes(), newClazz);
        }
        return md;
    }

    private static void initConstructor(CtClass superClazz, CtClass newClazz) throws NotFoundException,
                                                                             CannotCompileException {
        CtConstructor[] ctrs = superClazz.getConstructors();
        // �ж�clazz���޹��캯��������У�����һ���յĹ��캯�������������� super(null)�����������������ֱ�ӱ�ʵ����
        if (ctrs != null && ctrs.length != 0) {
            CtConstructor supCtr = ctrs[0];
            CtConstructor ctr = new CtConstructor(null, newClazz);
            List<String> params = Lists.newArrayList();
            for (CtClass _clazz : supCtr.getParameterTypes()) {
                params.add("null");
            }
            String body = "{super(" + StringUtils.join(params, GolbalConstants.ARRAY_SPLIT) + ");}";
            ctr.setBody(body);
            newClazz.addConstructor(ctr);
        }
    }

    private static CtMethod getMethod(CtClass superClazz, Method method) {
        List<CtClass> methodParamType = Lists.newArrayList();
        if (method.getParameterTypes() != null) {
            for (Class<?> clzz : method.getParameterTypes()) {
                try {
                    methodParamType.add(cp.get(clzz.getName()));
                } catch (NotFoundException e) {
                    try {
                        Class<?> clz = PluginFactory.loadClass(e.getMessage());
                        addClassPath(new LoaderClassPath(clz.getClassLoader()));
                        methodParamType.add(cp.get(clzz.getName()));
                    } catch (Exception e1) {
                    }
                }
            }
        }
        try {
            return superClazz.getDeclaredMethod(method.getName(), methodParamType.toArray(new CtClass[] {}));
        } catch (NotFoundException e) {
        }
        return null;
    }

    /**
     * �����������
     * 
     * @param clazz
     * @return
     */
    public static Object createProxyInstance(Class<?> clazz, String pluginID) {
        Object proxy;
        try {
            proxy = clazz.newInstance();
            // �滻Ϊ��̬AOP����
            Class<?> newC = ClassProxy.create(clazz, pluginID, true);
            Object newO = newC.newInstance();
            setProxyField(newO, proxy);
            return newO;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * ���ô������
     * 
     * @param obj
     * @param proxy
     */
    public static void setProxyField(Object obj, Object proxy) {
        setField(PROXY, obj, proxy);
    }

    /**
     * ���ò��IDֵ
     * 
     * @param obj
     * @param proxy
     */
    public static void setPluginIDField(Object obj, String pluginId) {
        setField(PLUGINID, obj, pluginId);
    }

    /**
     * ���ö���ı���
     * 
     * @param fieldName
     * @param obj
     * @param proxy
     */
    public static void setField(String fieldName, Object obj, Object proxy) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, proxy);
        } catch (Exception e) {
        }
    }

    /**
     * ��ȡ������ֵ
     * 
     * @param fieldName
     * @param obj
     * @return
     */
    public static Object getFieldVal(String fieldName, Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
        }
        return null;
    }

    public static String toClassName(String clazz) {
        return GolbalConstants.PATH_SPLIT + clazz.replace('.', '/');
    }

    /**
     * �õ������Nullֵ
     * 
     * @param clazz
     * @return
     */
    public static Object getNullVal(Class<?> clazz) {
        if (clazz == int.class) {
            return 0;
        }
        if (clazz == long.class) {
            return "0l";
        }
        if (clazz == double.class) {
            return "0.0";
        }
        if (clazz == float.class) {
            return "0.0f";
        }
        if (clazz == short.class) {
            return 0;
        }
        if (clazz == boolean.class) {
            return false;
        }
        if (clazz == byte.class) {
            return 0;
        }

        return null;
    }
}
