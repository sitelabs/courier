/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.model;

import java.io.Serializable;

import com.alibaba.china.courier.fastjson.JSON;

/**
 * json格式输出
 * 
 * <pre>
 * {
 *  isSuccess:true,
 *  errorMsg:"",
 *  errorCode:"",
 *  result:{//返回对象
 *      
 *  }
 * }
 * </pre>
 * 
 * @author joe 2012-2-24 上午10:33:03
 */
public class JsonResult implements Serializable {

    private static final long serialVersionUID  = -3673536077534024263L;

    // 1 开头的作为通用错误值使用
    public static final int   VALIDATION_FAILED = 101;

    // 2 开头的作为site错误值使用

    // 3 开头的作为page错误值使用

    // 4 开头的作为app错误值使用
    public static final int   APP_NOT_EXIST     = 401;

    private boolean           isSuccess;                                // 是否成功setErrorCode
    private Object            errorMsg;                                 // 错误消息
    private int               errorCode;                                // 错误代码
    private Object            result;                                   // 返回对象

    public JsonResult(boolean isSuccess){
        this.isSuccess = isSuccess;
    }

    public JsonResult(Object obj, boolean isSuccess){
        this.isSuccess = isSuccess;
    }

    public Object getErrorMsg() {
        return errorMsg;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setErrorMsg(Object errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
