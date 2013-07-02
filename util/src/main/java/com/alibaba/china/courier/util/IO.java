/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.china.courier.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.alibaba.china.courier.exception.UnexpectedException;

/**
 * 封装常用的io操作，包括文本读取、写入；文本流读取、写入；采用apache io lib
 * 
 * @author joe 2012-2-17 下午1:21:17
 */
public class IO {

    /**
     * 以 utf-8编码读取 文本流
     * 
     * @param
     * @return
     */
    public static String readContentAsString(InputStream is) {
        return readContentAsString(is, "utf-8");
    }

    /**
     * 读取文本流
     * 
     * @param
     * @return
     */
    public static String readContentAsString(InputStream is, String encoding) {
        String res = null;
        try {
            res = IOUtils.toString(is, encoding);
        } catch (Exception e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //
            }
        }
        return res;
    }

    /**
     * 以utf-8编码读取文件
     * 
     * @param file The file to read
     * @return The String content
     */
    public static String readContentAsString(File file) {
        return readContentAsString(file, "utf-8");
    }

    /**
     * 读取文本文件
     * 
     * @param
     * @return
     */
    public static String readContentAsString(File file, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            StringWriter result = new StringWriter();
            PrintWriter out = new PrintWriter(result);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
            String line = null;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    /**
     * 把文件读成字节
     * 
     * @param
     * @return
     */
    public static byte[] readContent(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] result = new byte[(int) file.length()];
            is.read(result);
            return result;
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    /**
     * 将文本内容写入到文件中，默认utf-8编码
     * 
     * @param
     * @param
     */
    public static void writeContent(String content, File file) {
        writeContent(content, file, "utf-8");
    }

    /**
     * 将文本内容写入到文件中
     * 
     * @param
     * @param
     */
    public static void writeContent(String content, File file, String encoding) {
        if (content == null) {
            return;
        }

        OutputStream os = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os, encoding));
            printWriter.print(content);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                if (os != null) os.close();
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 以utf-8编码把字符串写入到输出流
     * 
     * @param content The content to write
     * @param os The stream to write
     */
    public static void writeContent(String content, OutputStream os) {
        writeContent(content, os, "utf-8");
    }

    /**
     * 把字符串写入到输出流
     * 
     * @param
     * @param
     */
    public static void writeContent(String content, OutputStream os, String encoding) {
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os, encoding));
            printWriter.println(content);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 将字节写入到文件中
     * 
     * @param
     * @param
     */
    public static void write(byte[] data, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.flush();
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                if (os != null) os.close();
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 将输入流写入到输出流
     * 
     * @param is
     * @param os
     */
    public static void write(InputStream is, OutputStream os) {
        try {
            int read = 0;
            byte[] buffer = new byte[8096];
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //
            }
            try {
                os.close();
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 把输入流写入到文件中
     * 
     * @param is
     * @param f
     */
    public static void write(InputStream is, File f) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
            int read = 0;
            byte[] buffer = new byte[8096];
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //
            }
            try {
                if (os != null) os.close();
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 拷贝目录到另外一个目录，如果目标目录不存在，则会创建
     * 
     * @param source
     * @param target
     */
    public static void copyDirectory(File source, File target) {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }
            for (String child : source.list()) {
                copyDirectory(new File(source, child), new File(target, child));
            }
        } else {
            try {
                write(new FileInputStream(source), new FileOutputStream(target));
            } catch (IOException e) {
                throw new UnexpectedException(e);
            }
        }
    }

    /**
     * 创建文件夹
     * 
     * @param
     * @return 是否文件夹创建成功
     */
    public static boolean creatFolder(String actualPath) {
        File topFile = new File(actualPath);
        if (!topFile.exists()) {
            return topFile.mkdirs();
        }
        return true;
    }

    /**
     * 读取某个绝对路径下所有的文件夹和文件
     * 
     * @param
     * @return 是否文件夹创建成功
     */
    public static List<File> readFileandFolder(String actualPath) {
        List<File> list = new ArrayList<File>();
        return readFileandFolder(actualPath, list);
    }

    /**
     * 读取某个绝对路径下所有的文件夹和文件
     * 
     * @param
     * @return 是否文件夹创建成功
     */
    public static List<File> readFileandFolder(String actualPath, List<File> list) {
        File[] files = new File(actualPath).listFiles();
        if (files == null) {
            return list;
        }
        for (File file : files) {
            if (file.isFile()) {
                list.add(file);
            } else if (file.isDirectory()) {
                if (file.getName().startsWith(".")) {// 去掉.开头的文件夹，一般情况属于非正常文件夹
                    continue;
                }
                list.add(file);
                readFileandFolder(file.getAbsolutePath(), list);
            }
        }
        return list;

    }

    /**
     * 删除文件夹和内部所有文件
     */
    public static void deleteDirectory(String filePath) {
        try {
            FileUtils.deleteDirectory(new File(filePath));
        } catch (Exception e) {
        }
    }
}
