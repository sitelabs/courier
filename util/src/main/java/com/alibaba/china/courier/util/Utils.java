/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.china.courier.model.RequestQueryParam;
import com.google.common.collect.Maps;

public class Utils {

    public class GolbalConstants {

        public static final String PATH_SPLIT      = "/";

        public static final String PACKAGE_SPLIT   = ".";

        public static final String URI_SPLIT       = ":";

        public static final String STRIKE_SPLIT    = "-";

        public static final String UNDERLINE_SPLIT = "_";

        public static final String ARRAY_SPLIT     = ",";

        public static final String DEFAULT         = "default";

        public static final String MOCK            = "mock";

        public static final String TAGS_KEY        = "tags";
    }

    public static class ApplicationParamUtil {

        private static final ConcurrentHashMap<String, Object> contextParams = new ConcurrentHashMap<String, Object>();

        @SuppressWarnings("unchecked")
        public static <T> T getContextParam(String key) {
            return (T) contextParams.get(key);
        }

        public static void addContextParam(String key, Object obj) {
            contextParams.put(key, obj);
        }

        public static ConcurrentHashMap<String, Object> getContextParams() {
            return contextParams;
        }

    }

    public static class RequestParamUtil {

        private static final ThreadLocal<Map<String, Object>> contextCache = new ThreadLocal<Map<String, Object>>();

        public static final String                            QUERY_PARAM  = "queryParam";

        @SuppressWarnings("unchecked")
        public static <T> T getContextParam(String key) {

            return (T) getContextParams().get(key);
        }

        public static void addContextParam(String key, Object obj) {
            getContextParams().put(key, obj);
        }

        /**
         * 获取公共查询参数
         * 
         * @return
         */
        public static RequestQueryParam getQueryParam() {
            RequestQueryParam queryParam = getContextParam(QUERY_PARAM);
            if (queryParam == null) {
                queryParam = new RequestQueryParam();
                addContextParam(QUERY_PARAM, queryParam);
            }
            return queryParam;
        }

        public static Map<String, Object> getContextParams() {

            Map<String, Object> contextParams = contextCache.get();
            if (contextParams == null) {
                contextParams = Maps.newConcurrentMap();
                contextCache.set(contextParams);
            }

            return contextParams;
        }

        public static void clean() {
            contextCache.remove();
        }

    }

    private static final String DEFAULT_ENCODE = "GBK";

    public static String formatFilePath(String fileName) {
        return fileName.replaceAll("\\\\", GolbalConstants.PATH_SPLIT);
    }

    public static String normalizeAbsolutePath(String path) {
        String iPath = formatFilePath(path);
        if (!iPath.startsWith(GolbalConstants.PATH_SPLIT)) {
            iPath = GolbalConstants.PATH_SPLIT + iPath;
        }
        return iPath;
    }

    public static String getUserDir() {
        return System.getProperty("user.home");
    }

    public static String getFileCharset() {
        return System.getProperty("application.charset", DEFAULT_ENCODE);
    }

    public static String getHttpCharset() {
        return System.getProperty("http.charset", DEFAULT_ENCODE);
    }

    public static void main(String[] args) {
        System.out.println(normalizeAbsolutePath("aaa/fttr/a.txt"));
    }

}
