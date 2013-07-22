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

    private long    l;
    private double  d;
    private float   f;
    private short   s;

    private byte    b;

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

    /**
     * @return the l
     */
    public long getL() {
        return l;
    }

    /**
     * @param l the l to set
     */
    public void setL(long l) {
        this.l = l;
    }

    /**
     * @return the d
     */
    public double getD() {
        return d;
    }

    /**
     * @param d the d to set
     */
    public void setD(double d) {
        this.d = d;
    }

    /**
     * @return the f
     */
    public float getF() {
        return f;
    }

    /**
     * @param f the f to set
     */
    public void setF(float f) {
        this.f = f;
    }

    /**
     * @return the s
     */
    public short getS() {
        return s;
    }

    /**
     * @param s the s to set
     */
    public void setS(short s) {
        this.s = s;
    }

    /**
     * @return the b
     */
    public byte getB() {
        return b;
    }

    /**
     * @param b the b to set
     */
    public void setB(byte b) {
        this.b = b;
    }

}
