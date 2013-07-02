package com.alibaba.china.courier.model.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.china.courier.model.Resource;
import com.alibaba.china.courier.util.IO;
import com.alibaba.china.courier.util.Utils;

/**
 * 类FileResource.java的实现描述：<BR>
 * 文件资源, 读到资源时的来源是一个文件<BR>
 * 支持返回文件, String, JSON对象, 属性对象, 文件流等五种形式的内容返回
 * 
 * @author stan.liyh 2012-5-11 上午10:40:39
 */
public class FileResource extends AbstractResource {

    private static final long serialVersionUID = 1413861688480909348L;
    // 文件内容
    private File              file;
    // 文本内容
    protected String          stringContent;
    protected long            cacheCurTime     = 0;

    public FileResource(){
    }

    /**
     * 通过本地文件创建resource对象
     * 
     * @param file
     */
    public FileResource(File file){

        setFile(file);
    }

    protected void parseExtension() {
        if (name == null) {
            return;
        }

        int index = name.lastIndexOf(".");
        if (index < 0) {
            return;
        }

        this.extension = name.substring(index + 1, name.length());
    }

    @Override
    public String getAsString() {
        if (lastModified != getLastModified()) {
            try {
                stringContent = IO.readContentAsString(getAsInputStream(), Utils.getFileCharset());
            } catch (FileNotFoundException e) {

            }
            lastModified = file.lastModified();
        }

        if (stringContent != null) {
            return stringContent;
        }

        return null;
    }

    @Override
    public String getSimpleName() {
        return file.getName();
    }

    public InputStream getAsInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    @Override
    public boolean exist() {
        return file != null && file.exists();
    }

    @Override
    public long getLastModified() {
        if (file == null) {
            super.getLastModified();
        }

        long cur = System.currentTimeMillis();
        // 缓存10s，为了减少io读取
        if (cur - cacheCurTime < 10000 && lastModified != 0) {
            return lastModified;
        }
        cacheCurTime = cur;

        // lastModified = file.lastModified();

        return file.lastModified();
    }

    @Override
    public List<Resource> getChildren() {

        if (children.isEmpty() && file != null && file.isDirectory()) {
            for (File cf : file.listFiles()) {
                children.add(new FileResource(cf));
            }
        }
        return children;
    }

    private Map<String, Resource> cache = new HashMap<String, Resource>();

    /**
     * 添加子资源
     * 
     * @param res
     */
    public void addResource(Resource res) {

        if (!cache.containsKey(res.getName())) {
            cache.put(res.getName(), res);
            children.add(res);
        }
    }

    @Override
    public Resource getParent() {
        return new FileResource(file.getParentFile());
    }

    public void setFile(File file) {
        if (file == null) {
            return;
        }
        this.file = file;
        this.name = file.getPath();
        this.file = file;
        parseExtension();
        cacheCurTime = System.currentTimeMillis();

        this.isFolder = file.isDirectory();
    }

}
