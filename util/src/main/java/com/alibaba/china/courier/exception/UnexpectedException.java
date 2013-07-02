/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.exception;

/**
 * not know exception
 * 
 * @author joe 2012-2-17
 */
public class UnexpectedException extends RuntimeException {

    private static final long serialVersionUID = 3380424592239191007L;

    public UnexpectedException(String string){
        super(string);

    }

    public UnexpectedException(Throwable exception){
        super("Unexpected Error", exception);
    }

    /**
     * @param string
     * @param e
     */
    public UnexpectedException(String string, Exception e){
        super(string, e);
    }
}
