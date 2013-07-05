package com;

import com.alibaba.courier.plugin.DynamicBean;

public class Hello implements DynamicBean<Example> {

    public Boolean isHello;

    public void say(String world) {
        System.out.println("Hello" + world + ":");
    }

    public void say(int world) {
        System.out.println("Hello" + world + ":");
    }

    @Override
    public Example load() {
        // TODO Auto-generated method stub
        return null;
    }

}
