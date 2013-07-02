/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.module;

import java.util.List;

import com.alibaba.courier.module.model.Module;
import com.alibaba.courier.module.model.ModuleURI;

/**
 * the module container,provider module lifecyle manage
 * 
 * @author joe 2013-5-6 pm4:40:06
 */
public interface ModuleLifecycleContainer {

    /**
     * add module
     */
    public void add(Module<?> module);

    /**
     * upgrade one module
     * 
     * @param module
     */
    public void upgrade(Module<?> module);

    /**
     * get all module
     * 
     * @return
     */
    public List<Module<?>> getAll();

    /**
     * query module by uri
     * 
     * @param uri
     * @return
     */
    public Module<?> query(ModuleURI uri);

    /**
     * get references for the args of uri
     * 
     * @param uri
     * @return
     */
    public List<Module<?>> getRef(ModuleURI uri);

}
