
/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * ��DemoHandler.java��ʵ��������TODO ��ʵ������
 * 
 * @author joe 2013-7-1 ����5:13:15
 */
public class DemoHandler {

    public static void before(String methodInfo, Object obj) {

    }

    public static void after(String methodInfo) {
        // Method m = AOPUtil.getMethod(methodInfo);
        System.out.println("after");
    }

}
