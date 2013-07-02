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

import com.alibaba.china.courier.util.Utils;
import com.alibaba.courier.asm.ClassReader;
import com.alibaba.courier.asm.ClassWriter;

/**
 * ����asmʵ��AOP�Ĺ����࣬�߼���Դ��github
 * 
 * @author joe 2013-7-1 ����4:41:39
 */
public class ASMClassUtil {

    private static final String         SUFIX       = "$EnhancedByCourier";
    private static final BytecodeLoader classLoader = new BytecodeLoader();

    /**
     * <p>
     * ����asm��̬���ɵľ�̬������
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
        try {
            return (Class<T>) ASMClassUtil.getClassLoader().loadClass(enhancedClassName);
        } catch (ClassNotFoundException classNotFoundException) {
            ClassReader reader = null;
            try {
                reader = new ClassReader(clazz.getName());
            } catch (IOException ioexception) {
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassAdapter visitor = new ClassAdapter(enhancedClassName, clazz, writer);
            reader.accept(visitor, 0);
            byte[] byteCodes = writer.toByteArray();
            // writeClazz(enhancedClassName, byteCodes);
            Class<T> result = (Class<T>) getClassLoader().defineClass(enhancedClassName, byteCodes);
            return result;
        }
    }

    /**
     * <p>
     * �����ֽ������class
     * </p>
     */
    private static class BytecodeLoader extends ClassLoader {

        public Class<?> defineClass(String className, byte[] byteCodes) {
            return super.defineClass(className, byteCodes, 0, byteCodes.length);
        }
    }

    /**
     * <p>
     * ��ȡ{@link BytecodeLoader}
     * </p>
     * 
     * @see BytecodeLoader
     * @return
     */
    private static BytecodeLoader getClassLoader() {
        return classLoader;
    }

    /**
     * <p>
     * ��java�ֽ���д��class�ļ�
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
