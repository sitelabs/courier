import com.alibaba.courier.plugin.DefaultHttpChain;
import com.alibaba.courier.plugin.asm.ASMClassUtil;

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
 * @author joe 2013-7-2 下午2:47:46
 */
public class TestDemo {

    /**
     * @param args
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {

        String name = DefaultHttpChain.class.getName().replace('.', '/') + ".class";
        System.out.println(name);
        System.out.println(DefaultHttpChain.class.getClassLoader().getSystemResourceAsStream(name));
        Class<Demo> d = ASMClassUtil.getEnhancedClass(Demo.class);
        Demo demo = d.newInstance();
        demo.test("123");

    }
}
