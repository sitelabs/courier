import com.alibaba.courier.plugin.DynamicBean;

/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * ��DemoLoader.java��ʵ��������TODO ��ʵ������
 * 
 * @author joe 2013��7��18�� ����11:50:37
 * @param <T>
 */
public class DemoLoader implements DynamicBean<Demo> {

    public Demo load() {

        DemoImpl demo = new DemoImpl("this is cool");

        return demo;
    }

    public static DemoImpl test() {
        return null;
    }

}
