package com.alibaba.china.courier.model;

/**
 */
public class HttpParamConstants {

    /** 站点ID */
    public static final String SITE_ID                  = "site_id";

    /** 浏览者是否TP会员 */
    public static final String VISITOR_IS_TP            = "visitorIsTp";
    /** 浏览者IP */
    public static final String VISITOR_IP               = "visitorIp";
    public static final String CSRF_TOKEN               = "csrfToken";
    public static final String REFERER                  = "Referer";

    public static final String USER_AGENT_HEADER_KEY    = "User-Agent";

    /** 请求使用的域名，用于区分访问者来源 */
    public static final String SERVER_NAME              = "_server_name";
    /** 请求使用的端口，用于区分访问者来源 */
    public static final String SERVER_PORT              = "_server_port";

    public static final String LAST_LOGIN_ID_COOKIE_KEY = "__last_loginid__";

}
