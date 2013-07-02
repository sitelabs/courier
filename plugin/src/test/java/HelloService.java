import com.alibaba.courier.plugin.asm.PluginChecker;

/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
/**
 */
public class HelloService {

    Demo demo;

    public void test(String... strings) {

        PluginChecker.check(this);

    }

}
