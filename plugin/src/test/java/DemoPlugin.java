/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * ��DemoPlugin.java��ʵ��������TODO ��ʵ������
 * 
 * @author joe 2013��7��24�� ����1:22:23
 */
public class DemoPlugin {

    private Demo demo;

    public void test() {
        System.out.println(demo.getHellostr());
    }

    /**
     * @param demo the demo to set
     */
    public void setDemo(Demo demo) {
        this.demo = demo;
    }

}
