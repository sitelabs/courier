/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.model;

import java.util.List;

/**
 * 类Resource.java的实现描述：<BR>
 * 资源, 包括名称、内容、扩展名、最后修改时间。<BR>
 * 其中内容可以有多种形式的返回, 可以是字符串, 文件, 文件流, JSON对象, 属性对象, FileInfoModel。<BR>
 * 并不是每一种资源都支持上面这六种形式, 调用者必须清除自己读的是什么内容。<BR>
 * 如果不支持会抛出ResourceContentTypeNotSupportException
 * 
 * @author stan.liyh 2012-5-7 上午10:16:18
 */
public interface Resource {

    /**
     * 得到资源内容, 以字符串的形式返回
     * 
     * @return 文本内容
     * @throws ResourceException
     */

    public String getAsString();

    /**
     * 获取资源名称
     * 
     * @return
     */
    public String getName();

    /**
     * 获取资源的简单名称
     * 
     * @return
     */
    public String getSimpleName();

    /**
     * 获取资源扩展名
     * 
     * @return
     */
    public String getExtension();

    /**
     * 获取最好修改时间
     * 
     * @return
     */
    public long getLastModified();

    /**
     * 判断资源是否存在
     * 
     * @return
     */
    public boolean exist();

    /**
     * 对比资源是否为新的资源
     * 
     * @param old
     * @return
     */
    public boolean isNew(Resource old);

    /**
     * 是否是目录
     * 
     * @return
     */
    public boolean isFolder();

    /**
     * 获取当前资源的子资源
     * 
     * @return
     * @throws ResourceException
     */
    public List<Resource> getChildren();

    /**
     * 得到父资源
     * 
     * @return
     * @throws ResourceException
     */
    public Resource getParent();

}
