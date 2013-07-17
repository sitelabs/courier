/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.china.courier.model.HttpParamConstants;
import com.alibaba.china.courier.model.RequestQueryParam;
import com.alibaba.china.courier.util.CookieUtil;
import com.alibaba.china.courier.util.CsrfTokenUtil;
import com.alibaba.china.courier.util.DomainUtil;
import com.alibaba.china.courier.util.Utils.GolbalConstants;
import com.alibaba.china.courier.util.Utils.RequestParamUtil;

/**
 * 默认的http管道
 * 
 * @author joe 2013-6-28 下午12:58:02
 */
public class DefaultHttpChain implements HttpChain {

    @SuppressWarnings("rawtypes")
    public ChainReturn chain(HttpServletRequest request, HttpServletResponse resp) {

        RequestQueryParam requestParam = RequestParamUtil.getQueryParam();

        Enumeration enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String key = (String) enums.nextElement();
            requestParam.getParamters().put(key, request.getParameter(key));
        }

        requestParam.siteId = getSiteId(request);
        requestParam.userAgent = request.getHeader(HttpParamConstants.USER_AGENT_HEADER_KEY);

        String visitorIp = request.getParameter(HttpParamConstants.VISITOR_IP);
        if (StringUtils.isBlank(visitorIp)) {
            visitorIp = request.getRemoteAddr();
        }
        requestParam.visitorIp = visitorIp;

        requestParam.lastLoginId = getLastLoginId(request);

        String requestURL = request.getRequestURL().append("?").append(request.getQueryString()).toString();
        requestParam.url = requestURL;

        StringBuilder uri = new StringBuilder();
        uri.append(request.getScheme()).append(GolbalConstants.URI_SPLIT).append(GolbalConstants.PATH_SPLIT).append(GolbalConstants.PATH_SPLIT);
        uri.append(request.getServerName()).append(GolbalConstants.URI_SPLIT);
        uri.append(request.getServerPort()).append(GolbalConstants.PATH_SPLIT);

        requestParam.uri = uri.toString();

        requestParam.isTopDomain = DomainUtil.isRealTopdoamin(requestURL);
        requestParam.csrfToken = getCsrfToken(request);

        String serverName = request.getParameter(HttpParamConstants.SERVER_NAME);
        if (serverName == null) {
            serverName = request.getServerName();
        }
        requestParam.domain = serverName;

        String port = request.getParameter(HttpParamConstants.SERVER_PORT);
        if (port == null) {
            port = String.valueOf(request.getServerPort());
        }
        requestParam.port = port;

        String referer = request.getHeader(HttpParamConstants.REFERER);
        requestParam.referer = referer;
        return ChainReturn.NEXT;
    }

    public String getSiteId(HttpServletRequest request) {
        return request.getParameter(HttpParamConstants.SITE_ID);
    }

    public String getLastLoginId(HttpServletRequest request) {
        return CookieUtil.getCookieValue(HttpParamConstants.LAST_LOGIN_ID_COOKIE_KEY, request);
    }

    public String getCsrfToken(HttpServletRequest request) {
        return CsrfTokenUtil.getUniqueToken(request);
    }

    public static void main(String[] args) {

        String http = "http://design.1688.com/123.htm";

    }

}
