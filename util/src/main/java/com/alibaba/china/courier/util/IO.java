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
 * ��װ���õ�io�����������ı���ȡ��д�룻�ı�����ȡ��д�룻����apache io lib
 * 
 * @author joe 2012-2-17 ����1:21:17
 */
public class IO {

    /**
     * �� utf-8�����ȡ �ı���
     * 
     * @param
     * @return
     */
    public static String readContentAsString(InputStream is) {
        return readContentAsString(is, "utf-8");
    }

    /**
     * ��ȡ�ı���
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
     * ��utf-8�����ȡ�ļ�
     * 
     * @param file The file to read
     * @return The String content
     */
    public static String readContentAsString(File file) {
        return readContentAsString(file, "utf-8");
    }

    /**
     * ��ȡ�ı��ļ�
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
     * ���ļ������ֽ�
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
     * ���ı�����д�뵽�ļ��У�Ĭ��utf-8����
     * 
     * @param
     * @param
     */
    public static void writeContent(String content, File file) {
        writeContent(content, file, "utf-8");
    }

    /**
     * ���ı�����д�뵽�ļ���
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
     * ��utf-8������ַ���д�뵽�����
     * 
     * @param content The content to write
     * @param os The stream to write
     */
    public static void writeContent(String content, OutputStream os) {
        writeContent(content, os, "utf-8");
    }

    /**
     * ���ַ���д�뵽�����
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
     * ���ֽ�д�뵽�ļ���
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
     * ��������д�뵽�����
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
     * ��������д�뵽�ļ���
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
     * ����Ŀ¼������һ��Ŀ¼�����Ŀ��Ŀ¼�����ڣ���ᴴ��
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
     * �����ļ���
     * 
     * @param
     * @return �Ƿ��ļ��д����ɹ�
     */
    public static boolean creatFolder(String actualPath) {
        File topFile = new File(actualPath);
        if (!topFile.exists()) {
            return topFile.mkdirs();
        }
        return true;
    }

    /**
     * ��ȡĳ������·�������е��ļ��к��ļ�
     * 
     * @param
     * @return �Ƿ��ļ��д����ɹ�
     */
    public static List<File> readFileandFolder(String actualPath) {
        List<File> list = new ArrayList<File>();
        return readFileandFolder(actualPath, list);
    }

    /**
     * ��ȡĳ������·�������е��ļ��к��ļ�
     * 
     * @param
     * @return �Ƿ��ļ��д����ɹ�
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
                if (file.getName().startsWith(".")) {// ȥ��.��ͷ���ļ��У�һ��������ڷ������ļ���
                    continue;
                }
                list.add(file);
                readFileandFolder(file.getAbsolutePath(), list);
            }
        }
        return list;

    }

    /**
     * ɾ���ļ��к��ڲ������ļ�
     */
    public static void deleteDirectory(String filePath) {
        try {
            FileUtils.deleteDirectory(new File(filePath));
        } catch (Exception e) {
        }
    }
}
