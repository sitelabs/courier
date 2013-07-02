/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.model;

import org.apache.commons.lang3.StringUtils;

/**
 * the MimeType support for Request
 * 
 * @author joe 2013-6-25 pm3:35:59
 */
public enum MimeType {

    htm("text/html"), html("text/html"), json("text/json"), jsonp("text/javascript"), shtml("text/html"),
    empty("text/json");

    private String val;

    private MimeType(String val){
        this.val = val;
    }

    public static boolean accept(String type) {

        if (type == null) {
            return false;
        }

        String mimeType = StringUtils.lowerCase(type);
        for (MimeType _type : values()) {
            if (_type.name().equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    public static MimeType get(String type) {
        String mimeType = StringUtils.lowerCase(type);
        for (MimeType _type : values()) {
            if (_type.name().equals(mimeType)) {
                return _type;
            }
        }
        return empty;
    }

    public String getVal() {
        return val;
    }

}
