import com.alibaba.china.courier.util.Utils.RequestParamUtil;
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
 * 类Test.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年7月18日 下午8:51:40
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
        PluginFactory.instance = pluginFactory;
        pluginFactory.initPluginIoc();
        pluginFactory.initPlugin();

        DemoImpl demo = (DemoImpl) DynamicBeanUtil.getProxy("demo");

        demo = (DemoImpl) DynamicBeanUtil.getProxy("demo");
        System.out.println(demo);
        // demo = (DemoImpl) DynamicBeanUtil.getProxy("demo", demo);
        System.out.println(demo.getHellostr());
        System.out.println(demo.getHello().getWord());

        System.out.println(demo.getRefDemo().getRefStr());

        DemoPlugin dp = pluginFactory.getPlugin("demoPlugin");
        dp.test();
        dp.test();
        RequestParamUtil.clean();
        dp.test();

    }
}
