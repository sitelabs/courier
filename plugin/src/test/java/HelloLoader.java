import com.alibaba.courier.plugin.DynamicBean;

/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * 类HelloLoader.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年7月18日 下午11:53:06
 */
public class HelloLoader implements DynamicBean<Hello> {

    public Hello load() {
        Hello hello = new Hello();
        hello.setWord(" this is world!");
        System.out.println("hello.load");
        return hello;
    }

    public String[] getS() {
        return null;
    }

}
