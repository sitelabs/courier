/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * load the property file
 * 
 * @author joe 2013-6-24 pm1:46:46
 */
public class PropertyLoader {

    /**
     * load the file in you user root dir
     * 
     * @param name
     * @return
     */
    public static Properties loadByUserDir(String name) {
        Properties pro = new Properties();
        File local = new File(Utils.getUserDir(), name);
        if (!local.exists()) {
            return pro;
        }
        try {
            pro.load(new FileInputStream(local));
        } catch (IOException e) {
        }
        return pro;
    }

}
