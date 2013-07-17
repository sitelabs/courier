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
 * 提供Http链式执行服务
 * 
 * @author joe 2013-6-25 下午4:24:55
 */
public interface HttpChain {

    public ChainReturn chain(HttpServletRequest req, HttpServletResponse resp);

    /**
     * chain的返回类型
     * 
     * @author joe 2013年7月17日 上午10:04:11
     */
    public enum ChainReturn {
        BREAK, NEXT
    }
}
