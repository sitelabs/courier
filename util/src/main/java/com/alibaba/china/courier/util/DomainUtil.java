/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

/**
 * @author joe 2013-6-27 下午11:58:14
 */
public class DomainUtil {

    private static final String[] domainList = { ".com", ".co", ".net", ".org", ".cn", ".biz", ".com.cn", ".net.cn",
            ".org.cn", ".gov.cn", ".name", ".info", ".me", ".so", ".tel", ".mobi", ".asia", ".cc", ".tv", ".hk" };

    /**
     * 根据当前的访问url的后缀来判断是否是真实的顶级域名
     * 
     * @param domain
     * @return
     */
    public static boolean isRealTopdoamin(String requestURL) {

        boolean isTopdomain = false;

        try {
            URL url = new URL(requestURL);
            String host = url.getHost();

            for (String dl : domainList) {
                if (host.endsWith(dl)) {
                    host = host.replaceAll(dl, StringUtils.EMPTY);
                    String[] split = host.split("\\.");
                    // 1688.com
                    if (split.length == 1) {
                        isTopdomain = true;
                    }
                    if (split.length == 2 && split[0].equals("www")) {
                        // www.1688.com
                        isTopdomain = true;
                    }
                    break;
                }
            }

        } catch (MalformedURLException e) {
        }

        return isTopdomain;
    }

    public static void main(String[] args) {
        System.out.println(isRealTopdoamin("http://1688.com"));
    }
}
