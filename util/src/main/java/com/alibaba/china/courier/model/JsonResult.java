/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.model;

import java.io.Serializable;

import com.alibaba.china.courier.fastjson.JSON;

/**
 * json��ʽ���
 * 
 * <pre>
 * {
 *  isSuccess:true,
 *  errorMsg:"",
 *  errorCode:"",
 *  result:{//���ض���
 *      
 *  }
 * }
 * </pre>
 * 
 * @author joe 2012-2-24 ����10:33:03
 */
public class JsonResult implements Serializable {

    private static final long serialVersionUID  = -3673536077534024263L;

    // 1 ��ͷ����Ϊͨ�ô���ֵʹ��
    public static final int   VALIDATION_FAILED = 101;

    // 2 ��ͷ����Ϊsite����ֵʹ��

    // 3 ��ͷ����Ϊpage����ֵʹ��

    // 4 ��ͷ����Ϊapp����ֵʹ��
    public static final int   APP_NOT_EXIST     = 401;

    private boolean           isSuccess;                                // �Ƿ�ɹ�setErrorCode
    private Object            errorMsg;                                 // ������Ϣ
    private int               errorCode;                                // �������
    private Object            result;                                   // ���ض���

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
