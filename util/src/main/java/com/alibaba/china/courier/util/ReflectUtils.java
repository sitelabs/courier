package com.alibaba.china.courier.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * ���乤����
 */
public final class ReflectUtils {

    private ReflectUtils(){
    }

    /**
     * ��ȡobj����fieldName��Field �ܻ�ȡ��������ԣ����ܷ��ʽӿ��е�����
     * 
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field getFieldByName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        return null;
    }

    /**
     * ��ȡ����field����������(��������Object��ͽӿ��е�����) ,����������static,final
     * 
     * @param clazz
     * @return
     */
    public static Field[] getVariableFields(Class<?> clazz) {
        ArrayList<Field> list = new ArrayList<Field>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field f : fields) {
                if (!isConstant(f)) {
                    list.add(f);
                }
            }
        }
        return list.toArray(new Field[0]);

    }

    /**
     * ��ȡ����field����������(��������Object��ͽӿ��е�����) ,������final����
     * 
     * @param clazz
     * @return
     */
    public static Field[] getNotFinalFields(Class<?> clazz) {
        ArrayList<Field> list = new ArrayList<Field>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field[] fields = superClass.getDeclaredFields();
                for (Field f : fields) {
                    if (!Modifier.isFinal(f.getModifiers())) {
                        list.add(f);
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        return list.toArray(new Field[0]);

    }

    /**
     * ��ȡobj����fieldName��Field �ܻ�ȡ��������ԣ����ܷ��ʽӿ��е�����
     * 
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getValueByFieldName(Object obj, String fieldName) {
        try {
            return get(obj, getFieldByName(obj, fieldName));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * ���ö���ָ�����Ե�ֵ
     * 
     * @param obj
     * @param f
     * @param value
     */
    public static void set(Object obj, Field f, Object value) {
        try {
            if (f.isAccessible()) {
                f.set(obj, value);
            } else {
                f.setAccessible(true);
                f.set(obj, value);
                f.setAccessible(false);
            }
        } catch (Exception e) {
        }
    }

    /**
     * ��ȡ�����ָ������
     * 
     * @param obj
     * @param f
     * @return the obj
     */
    public static Object get(Object obj, Field f) {
        if (f == null) {
            return null;
        }
        Object value = null;
        try {
            if (f.isAccessible()) {
                value = f.get(obj);
            } else {
                f.setAccessible(true);
                value = f.get(obj);
                f.setAccessible(false);
            }
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * ���ö���ָ�����Ե�ֵ
     * 
     * @see #getFieldByName(Object, String)
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void set(Object obj, String fieldName, Object value) {
        try {
            Field f = getFieldByName(obj, fieldName);
            if (f == null) {
                return;
            }
            if (f.isAccessible()) {
                f.set(obj, value);
            } else {
                f.setAccessible(true);
                f.set(obj, value);
                f.setAccessible(false);
            }
        } catch (Exception e) {
        }
    }

    /**
     * �������Ƿ��ǳ���(static ���� final)
     * 
     * @param f
     * @return
     */
    private static boolean isConstant(Field f) {
        return Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers());
    }

    /**
     * ��ȡ����method����������(��������Object��ķ���)
     * 
     * @param clazz
     * @return
     */
    public static Method[] getVariableMethods(Class<?> clazz) {
        ArrayList<Method> list = new ArrayList<Method>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            Method[] methods = superClass.getDeclaredMethods();
            for (Method m : methods) {
                list.add(m);
            }
        }
        return list.toArray(new Method[0]);

    }
}
