/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �ṩHttp��ʽִ�з���
 * 
 * @author joe 2013-6-25 ����4:24:55
 */
public interface HttpChain {

    public ChainReturn chain(HttpServletRequest req, HttpServletResponse resp);

    /**
     * chain�ķ�������
     * 
     * @author joe 2013��7��17�� ����10:04:11
     */
    public enum ChainReturn {
        BREAK, NEXT
    }
}
