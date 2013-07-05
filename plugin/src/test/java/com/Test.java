package com;

import com.alibaba.courier.plugin.proxy.ClassProxy;

public class Test {

    public static void main(String[] args) throws Exception {

        Hello hello = new Hello();
        hello.isHello = true;

        // CtMethod md =
        // CtNewMethod.make(" public void say(String world){System.out.println(\"ffff\");proxy.say(world);}",
        // cc);
        // cc.addMethod(md);

        Hello newH = (Hello) ClassProxy.create(Hello.class).newInstance();
        newH.getClass().getDeclaredField("proxy").set(newH, hello);
        newH.say("joe test");
    }
}
