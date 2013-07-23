import com.alibaba.courier.plugin.DynamicBeanUtil;
import com.alibaba.courier.plugin.PluginFactory;

/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */

/**
 * ��Test.java��ʵ��������TODO ��ʵ������
 * 
 * @author joe 2013��7��18�� ����8:51:40
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        // Class<DemoImpl> clzz = ClassProxy.create(DemoImpl.class);
        // DemoImpl di = clzz.newInstance();
        // System.out.println(di.getDL());
        PluginFactory pluginFactory = new PluginFactory();
        pluginFactory.initContainer();
        pluginFactory.initPluginIoc();
        pluginFactory.initPlugin();
        PluginFactory.instance = pluginFactory;

        DemoImpl demo = (DemoImpl) DynamicBeanUtil.getProxy("demo", null);

        // demo = (DemoImpl) DynamicBeanUtil.getProxy("demo", demo);
        System.out.println(demo.getHellostr() + ":" + demo.getHello().getWord() + ";" + demo.getDL());

        // System.out.println(refDemo.getRefStr());

    }

}
