/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.model.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.china.courier.model.Resource;
import com.alibaba.china.courier.util.Utils;

/**
 * @author joe 2013-1-23 pm8:50:18
 */
public class StringResource extends AbstractResource {

    private static final long serialVersionUID = 882951769951923098L;

    private String            stringContent;

    private String            simpleName;

    private Resource          parent;

    public StringResource(){
    }

    public StringResource(String name){
        this.name = Utils.formatFilePath(name);
        this.isFolder = true;
        parseName();
    }

    public StringResource(String name, String content){
        this.name = Utils.formatFilePath(name);
        this.stringContent = content;
        this.lastModified = System.currentTimeMillis();
        if (StringUtils.isNotBlank(content)) {
            this.isFolder = true;
        }
        parseName();
    }

    /**
     */
    private void parseName() {
        simpleName = FilenameUtils.getName(name);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        parseName();
    }

    @Override
    public String getAsString() {
        return stringContent;
    }

    @Override
    public boolean exist() {
        return false;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public void setStringContent(String stringContent) {
        this.stringContent = stringContent;
    }

    @Override
    public Resource getParent() {
        return this.parent;
    }

    private Map<String, Resource> cache = new HashMap<String, Resource>();

    /**
     * @param res
     */
    public void addResource(Resource res) {

        if (!cache.containsKey(res.getName())) {
            cache.put(res.getName(), res);
            children.add(res);
        }
    }

    @Override
    public List<Resource> getChildren() {
        return children;
    }

    public static void main(String[] args) {
        StringResource sr = new StringResource("name/test");
        System.out.println(sr.getSimpleName());
    }

}
