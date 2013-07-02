/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.model;

import java.util.List;

/**
 * ��Resource.java��ʵ��������<BR>
 * ��Դ, �������ơ����ݡ���չ��������޸�ʱ�䡣<BR>
 * �������ݿ����ж�����ʽ�ķ���, �������ַ���, �ļ�, �ļ���, JSON����, ���Զ���, FileInfoModel��<BR>
 * ������ÿһ����Դ��֧��������������ʽ, �����߱�������Լ�������ʲô���ݡ�<BR>
 * �����֧�ֻ��׳�ResourceContentTypeNotSupportException
 * 
 * @author stan.liyh 2012-5-7 ����10:16:18
 */
public interface Resource {

    /**
     * �õ���Դ����, ���ַ�������ʽ����
     * 
     * @return �ı�����
     * @throws ResourceException
     */

    public String getAsString();

    /**
     * ��ȡ��Դ����
     * 
     * @return
     */
    public String getName();

    /**
     * ��ȡ��Դ�ļ�����
     * 
     * @return
     */
    public String getSimpleName();

    /**
     * ��ȡ��Դ��չ��
     * 
     * @return
     */
    public String getExtension();

    /**
     * ��ȡ����޸�ʱ��
     * 
     * @return
     */
    public long getLastModified();

    /**
     * �ж���Դ�Ƿ����
     * 
     * @return
     */
    public boolean exist();

    /**
     * �Ա���Դ�Ƿ�Ϊ�µ���Դ
     * 
     * @param old
     * @return
     */
    public boolean isNew(Resource old);

    /**
     * �Ƿ���Ŀ¼
     * 
     * @return
     */
    public boolean isFolder();

    /**
     * ��ȡ��ǰ��Դ������Դ
     * 
     * @return
     * @throws ResourceException
     */
    public List<Resource> getChildren();

    /**
     * �õ�����Դ
     * 
     * @return
     * @throws ResourceException
     */
    public Resource getParent();

}
