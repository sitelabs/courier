/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.plugin.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.china.courier.util.Utils.ApplicationParamUtil;

/**
 * Spring适配器，用来加载spring的bean对象，统一给小鸡管理
 * 
 * @author joe 2013-7-2 下午5:01:47
 */
public class SpringAdapter implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String string : beanNames) {
            try {
                Object obj = applicationContext.getBean(string);
                ApplicationParamUtil.addBean(string, obj);
            } catch (Exception e) {
            }
        }
    }

}
