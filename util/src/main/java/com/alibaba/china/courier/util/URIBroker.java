package com.alibaba.china.courier.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Maps;

public class URIBroker {

    private static final String PATH_SEPERATOR = "/";

    private String              serverInfo;                              // http://xxx.cn.alibaba.com:4100/

    private String              pathInfo;                                // esite/esite_config.htm

    private LinkedHashMap       query          = Maps.newLinkedHashMap();

    public URIBroker(String serverInfo, String pathInfo){
        this.serverInfo = normalizeServerInfo(serverInfo);
        this.pathInfo = normalizePathInfo(pathInfo);
    }

    public URIBroker fork() {
        URIBroker fork = new URIBroker(this.serverInfo, this.pathInfo);

        for (Iterator i = query.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            fork.getQuery().put(entry.getKey(), entry.getValue());
        }

        return fork;
    }

    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.serverInfo).append(this.pathInfo);
        renderQuery(builder);

        return builder.toString();

    }

    public String toString() {
        return this.render();
    }

    private void renderQuery(StringBuilder builder) {
        if (!query.isEmpty()) {
            builder.append("?");

            for (Iterator i = query.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String id = (String) entry.getKey();
                String value = (String) entry.getValue();

                builder.append(escapeURL(id)).append("=").append(escapeURL(value));

                if (i.hasNext()) {
                    builder.append("&");
                }
            }
        }
    }

    public URIBroker addQueryData(String id, String value) {
        if (id == null) {
            id = "";
        } else {
            id = id.trim();
        }

        if (id.length() > 0) {
            if (value == null) {
                value = "";
            }

            this.query.put(id, value);
        }

        return this;
    }

    private String escapeURL(String str) {
        return StringEscapeUtils.escapeHtml4(str);
    }

    private String normalizeServerInfo(String original) {
        if (original.endsWith(PATH_SEPERATOR)) {
            return original;
        }
        return original + PATH_SEPERATOR;
    }

    private String normalizePathInfo(String original) {
        if (original.startsWith(PATH_SEPERATOR)) {
            return original.substring(1);
        }
        return original;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public LinkedHashMap getQuery() {
        return query;
    }

    public void setQuery(LinkedHashMap query) {
        this.query = query;
    }

}
