/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.model;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * the Request Param model
 * 
 * @author joe 2013-6-25 下午4:39:50
 */
public class RequestQueryParam implements Serializable, Cloneable {

    private static final long   serialVersionUID = 5468265670116871387L;

    public MimeType             mimeType;                               // 请求的类型

    public boolean              logined;                                // 是否登陆

    public String               uid;                                    // 会员的唯一id

    public String               loginId;                                // 登录id

    public String               lastLoginId;                            // 上一次登录id

    public String               visitorIp;                              // 当前浏览者的Ip地址

    public String               userAgent;

    public String               referer;                                // 来源

    public boolean              isTopDomain;

    public String               siteId;

    public String               csrfToken;

    public String               domain;                                 // the easy domain example: 123.com

    public String               port;                                   // the request url port

    public String               url;                                    // the visit url example:
                                                                         // http://www.123.com/124.htm?123=val

    public String               uri;                                    // example : http://www.123.com/

    private Map<String, Object> paramters;

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLastLoginId() {
        return lastLoginId;
    }

    public void setLastLoginId(String lastLoginId) {
        this.lastLoginId = lastLoginId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isTopDomain() {
        return isTopDomain;
    }

    public void setTopDomain(boolean isTopDomain) {
        this.isTopDomain = isTopDomain;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVisitorIp() {
        return visitorIp;
    }

    public void setVisitorIp(String visitorIp) {
        this.visitorIp = visitorIp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public Map<String, Object> getParamters() {
        if (paramters == null) {
            paramters = Maps.newConcurrentMap();
        }
        return paramters;
    }

    public void setParamters(Map<String, Object> paramters) {
        this.paramters = paramters;
    }

}
