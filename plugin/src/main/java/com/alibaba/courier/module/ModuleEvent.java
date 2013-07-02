/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.module;

/**
 * module的事件
 * 
 * @author joe 2013-6-3 下午4:07:10
 */
public class ModuleEvent {

    public static final String UPGRADE = "upgrade";

    public static final String DELETE  = "delete";

    private String             type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
