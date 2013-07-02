/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin;

import java.lang.annotation.Annotation;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 */

public class PluginFactoryTest extends TestCase {

    PluginFactory       _pluginFactory = new PluginFactory();
    List<PluginFactory> test;

    @Override
    protected void setUp() throws Exception {

        _pluginFactory.initContainer();
        _pluginFactory.initPlugin();
    }

    public void testConfig() throws SecurityException, NoSuchFieldException {

        Annotation[] as = PluginFactoryTest.class.getDeclaredField("test").getAnnotations();
        for (Annotation annotation : as) {
            System.out.println(annotation.toString());

        }

        PluginFactory pluginFactory = _pluginFactory.getPlugin("pluginFactory");
        Assert.assertNotNull(pluginFactory);
    }

}
