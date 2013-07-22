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
public class RefDemoLoader implements DynamicBean<RefDemo> {

    private Demo demo;

    public RefDemo load() {
        // RefDemo hello = new RefDemo();
        // hello.setRefStr("ref" + demo.getHellostr());
        // System.out.println("RefDemo.load");
        // return hello;
        return null;
    }

    public void setDemo(Demo demo) {
        this.demo = demo;
    }

}
