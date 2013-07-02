/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * ��CsrfTockenUtil.java��ʵ��������������webx��ʵ��
 * 
 * @author joe 2012-6-16 ����3:00:32
 */
public class CsrfTokenUtil {

    public static final String DEFAULT_TOKEN_KEY = "_csrf_token";

    private CsrfTokenUtil(){
    }

    /**
     * �޸�Ϊ��cookieȡ,û���򴴽���token
     * 
     * @return
     */
    public static String getUniqueToken(HttpServletRequest request) {
        HttpSession session = request.getSession();

        String tokenOfSession = StringUtils.trimToNull((String) session.getAttribute(DEFAULT_TOKEN_KEY));

        if (tokenOfSession == null || isExpire(tokenOfSession)) {
            tokenOfSession = newToken();
            session.setAttribute(DEFAULT_TOKEN_KEY, tokenOfSession);
        }

        return hex(tokenOfSession);
    }

    /**
     * ���request��session�е�csrftoken��ֵֻ����ͬ,ֻ����request��session��ֵ�����Ҳ�Ϊ��������򷵻�true
     * 
     * @param runData
     * @return
     */
    public static boolean check(HttpServletRequest request) {
        String fromRequest = StringUtils.trimToNull(request.getParameter(DEFAULT_TOKEN_KEY));

        // if request have no crsfkey return false
        if (fromRequest == null) {
            return false;
        }

        // get csrfkey from session
        HttpSession session = request.getSession();
        String fromSession = StringUtils.trimToNull((String) session.getAttribute(DEFAULT_TOKEN_KEY));
        if (fromSession == null) {
            return false;
        }

        if (isExpire(fromSession)) {
            return false;
        }

        fromSession = hex(fromSession);

        // judge csrfkey in request to csrfKey in session
        boolean result = fromRequest.equals(fromSession);
        if (!result) {
        }
        return result;

    }

    private static String newToken() {
        long longValue = System.currentTimeMillis();
        return Long.toString(longValue);
    }

    private static String hex(String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(value.getBytes());
            return new String(Hex.encodeHex(bytes));
        } catch (NoSuchAlgorithmException e) {
            return value;
        }

    }

    private static boolean isExpire(String tokenOfSession) {
        long longValue = 0L;
        try {
            longValue = Long.parseLong(tokenOfSession);
        } catch (NumberFormatException e) {
            return true;
        }
        long nowValue = System.currentTimeMillis();
        long tmp = nowValue - longValue;
        // ��ǰʱ��С��token��ʱ��,����
        if (tmp < 0L) {
            return true;
        }
        // ��ǰʱ�����token 24Сʱ,����
        return tmp > 24L * 60 * 60 * 1000;
    }

}
