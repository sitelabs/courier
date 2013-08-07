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

    private static final String[] domainList    = { ".com.cn", ".com", ".co", ".net", ".org", ".cn", ".biz", ".net.cn",
            ".org.cn", ".gov.cn", ".name", ".info", ".me", ".so", ".tel", ".mobi", ".asia", ".cc", ".tv", ".hk" };

    private static final String[] privateDomain = { "1688", "alibaba", "taobao" };

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
                    String temp = host.replaceAll(dl, StringUtils.EMPTY);
                    String[] split = temp.split("\\.");
                    // 1688.com
                    if (split.length == 1) {
                        isTopdomain = true;
                        return true;
                    }
                    if (split.length == 2) {
                        if (split[0].equals("www")) {
                            // www.1688.com
                            isTopdomain = true;
                            return true;
                        } else {
                            // 如果域名不是阿里系的域名，则都认为是顶级域名
                            boolean isAlibaba = false;
                            for (String pd : privateDomain) {
                                if (split[1].equals(pd)) {
                                    isAlibaba = true;
                                    break;
                                }
                            }
                            if (!isAlibaba) {
                                return true;
                            }
                        }
                    }
                }
            }

        } catch (MalformedURLException e) {
        }

        return isTopdomain;
    }

    public static void main(String[] args) {
        System.out.println(isRealTopdoamin("http://www.guolinjixie.com.cn/"));
    }
}
