package com.alibaba.china.courier.model.resource;

/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.china.courier.model.Resource;

/**
 * 类Resource.java的实现描述：文件资源对象
 * 
 * @author stan.liyh 2012-5-7 上午10:16:18
 */
abstract public class AbstractResource implements Resource, Serializable {

    private static final long serialVersionUID = -1669211528258841728L;
    // 文件名，如果需要包含完整路径,必须要保证整个全局唯一
    protected String          name;
    // 文件扩展名
    protected String          extension;
    // 时间戳
    protected long            lastModified     = 0;

    protected boolean         isFolder         = false;

    protected List<Resource>  children         = new ArrayList<Resource>();

    public String getName() {
        return name;
    }

    public String getExtension() {

        if (StringUtils.isBlank(this.extension)) {

            int pos = StringUtils.lastIndexOf(getName(), ".");
            if (pos > 0) {
                this.extension = StringUtils.substring(getName(), (pos + 1));
            }
        }

        return extension;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public boolean isNew(Resource old) {
        return this.getLastModified() == old.getLastModified();
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void isFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public boolean isFolder() {
        return this.isFolder;
    }

    public Resource getParent() {
        return null;
    }

    public List<Resource> getChildren() {
        return Collections.EMPTY_LIST;
    }

}
