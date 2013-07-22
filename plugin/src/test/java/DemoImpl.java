/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * 类DemoImpl.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年7月18日 下午9:05:40
 */
public class DemoImpl extends Demo {

    /**
     * @param hellostr
     */
    public DemoImpl(String hellostr){
        super(hellostr);
    }

    // public DemoImpl(){
    // super(null);
    // }

    private Hello   hello;

    private boolean bol;

    private RefDemo refDemo;

    /**
     * @return the hello
     */
    public Hello getHello() {
        return hello;
    }

    /**
     * @param hello the hello to set
     */
    public void setHello(Hello hello) {
        this.hello = hello;
    }

    /**
     * @return the bol
     */
    public boolean isBol() {
        return bol;
    }

    /**
     * @param bol the bol to set
     */
    public void setBol(boolean bol) {
        this.bol = bol;
    }

    /**
     * @return the refDemo
     */
    public RefDemo getRefDemo() {
        return refDemo;
    }

    /**
     * @param refDemo the refDemo to set
     */
    public void setRefDemo(RefDemo refDemo) {
        this.refDemo = refDemo;
    }

}
