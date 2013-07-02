/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author joe 2013-5-13 pm4:14:03
 */
public class PluginWebListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PluginFactory pluginFactory = new PluginFactory();
        pluginFactory.initContainer();
        pluginFactory.initPlugin();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
