/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.asm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.alibaba.china.courier.util.Utils;
import com.alibaba.courier.asm.ClassReader;
import com.alibaba.courier.asm.ClassWriter;
import com.google.common.collect.Maps;

/**
 * 利用asm实现AOP的工具类，逻辑来源于github
 * 
 * @author joe 2013-7-1 下午4:41:39
 */
public class ASMClassUtil {

    private static final String                      SUFIX        = "$EnhancedByCourier";
    private static final Map<String, BytecodeLoader> classLoaders = Maps.newConcurrentMap();

    /**
     * <p>
     * 返回asm动态生成的静态代理类
     * </p>
     * 
     * @param <T>
     * @param clazz
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getEnhancedClass(Class<T> clazz) {
        String enhancedClassName = clazz.getName() + SUFIX;
        String clname = clazz.getClassLoader().toString();
        BytecodeLoader classloader = null;
        if (classLoaders.containsKey(clname)) {
            classloader = classLoaders.get(clname);
        } else {
            classloader = new BytecodeLoader(clazz.getClassLoader());
            classLoaders.put(clname, classloader);
        }
        try {
            return (Class<T>) classloader.loadClass(enhancedClassName);
        } catch (ClassNotFoundException classNotFoundException) {
            ClassReader reader = null;
            try {
                String name = clazz.getName().replace('.', '/') + ".class";
                InputStream in = clazz.getClassLoader().getResource(name).openStream();
                reader = new ClassReader(in);
            } catch (IOException ioexception) {
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassAdapter visitor = new ClassAdapter(enhancedClassName, clazz, writer);
            reader.accept(visitor, 0);
            byte[] byteCodes = writer.toByteArray();
            // writeClazz(enhancedClassName, byteCodes);

            Class<T> result = (Class<T>) classloader.defineClass(enhancedClassName, byteCodes);
            return result;
        }
    }

    /**
     * <p>
     * 根据字节码加载class
     * </p>
     */
    private static class BytecodeLoader extends ClassLoader {

        public BytecodeLoader(ClassLoader cl){
            super(cl);
        }

        public Class<?> defineClass(String className, byte[] byteCodes) {
            return super.defineClass(className, byteCodes, 0, byteCodes.length);
        }
    }

    /**
     * <p>
     * 把java字节码写入class文件
     * </p>
     * 
     * @param <T>
     * @param name
     * @param data
     */
    private static <T> void writeClazz(String name, byte[] data) {
        try {
            File file = new File(Utils.getUserDir(), ".temp/" + name + ".class");
            if (!file.exists()) {
                file.mkdir();
            } else {
                file.delete();
            }

            FileOutputStream fout = new FileOutputStream(file);

            fout.write(data);
            fout.close();
        } catch (Exception e) {
        }
    }

}
