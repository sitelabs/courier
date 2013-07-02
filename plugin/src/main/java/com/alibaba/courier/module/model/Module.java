/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.courier.module.model;

import java.io.Serializable;

import com.alibaba.china.courier.model.Resource;

/**
 * DataModule entity
 * 
 * @author joe 2013-5-3 pm5:26:10
 */
public class Module<T> implements Serializable {

    private static final long serialVersionUID = 8811874810424752962L;

    private ModuleURI         uri;                                    // the unique id

    private Origin            origion;                                // file origin

    private String            protocol;                               // the type of dataModule:dm,ds,lib

    private T                 property;                               // the entity property in data module

    private Resource          resource;                               // the module resource may be jar or files

    public ModuleURI getUri() {
        return uri;
    }

    public void setUri(ModuleURI uri) {
        this.uri = uri;
    }

    public Origin getOrigion() {
        return origion;
    }

    public void setOrigion(Origin origion) {
        this.origion = origion;
    }

    public T getProperty() {
        return property;
    }

    public void setProperty(T property) {
        this.property = property;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
