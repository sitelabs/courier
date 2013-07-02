/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.module.model;

import java.io.Serializable;

/**
 * the URI of module,find the module from the uri
 * 
 * @author joe 2013-5-5 am10:22:14
 */
public class ModuleURI implements Serializable {

    private static final long serialVersionUID = 7940667450879267348L;
    private String            name;                                   // unique name
    private String            namespace        = "default";

    private String            version          = "1.0.0";

    public ModuleURI(String namespace, String name, String version){

        if (namespace != null) {
            this.namespace = namespace;
        }
        if (version != null) {
            this.version = version;
        }
        this.name = name;
    }

    /**
     * create the default URI
     * 
     * @param name
     * @return
     */
    public static ModuleURI create(String name) {
        return new ModuleURI(null, name, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
