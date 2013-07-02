/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.util.groovy;

import groovy.lang.GroovyObject;
import groovy.lang.Script;

import java.util.Map;

import com.alibaba.china.courier.util.Utils.RequestParamUtil;

/**
 * 类GroovyClass.java的实现描述：封装编译后的groovy代码，支持script或者class
 * 
 * @author joe 2012-4-30 下午11:37:37
 */
public class GroovyClass<T> {

    private T    object;

    private long lastmodified = 0;

    /**
     * 
     */
    public GroovyClass(T script){
        this.object = script;
    }

    public long getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(long lastmodified) {
        this.lastmodified = lastmodified;
    }

    /**
     * 正常执行某个方法
     * 
     * @param methosName
     * @param args
     * @return
     */
    public Object invoke(String methosName, Object[] args) {
        if (object instanceof Script) {
            return ((Script) object).invokeMethod(methosName, args);
        }
        if (object instanceof GroovyObject) {
            return ((GroovyObject) object).invokeMethod(methosName, args);

        }
        return null;
    }

    public Object get() {

        if (object instanceof Script) {
            Script script = (Script) object;
            script.getBinding().getVariables().putAll(RequestParamUtil.getContextParams());
            Object obj = script.run();
            script = null;// 将script对象赋予null
            return obj;
        }

        return object;
    }

    public Object get(Map binds) {

        if (object instanceof Script) {
            Script script = (Script) object;
            script.getBinding().getVariables().putAll(RequestParamUtil.getContextParams());
            script.getBinding().getVariables().putAll(binds);
            Object obj = script.run();
            script = null;// 将script对象赋予null
            return obj;
        }

        return object;
    }

}
