import com.alibaba.courier.plugin.DynamicBean;

/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 * 类Demo.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013-7-1 下午5:11:15
 */
public class Demo implements DynamicBean<HelloService> {

    public void init() {

    }

    public void test(String... strings) {
        System.out.println("test" + strings.length);
    }

    /*
     * (non-Javadoc)
     * @see com.alibaba.courier.plugin.DynamicBean#load()
     */
    @Override
    public HelloService load() {
        // TODO Auto-generated method stub
        return null;
    }

}
