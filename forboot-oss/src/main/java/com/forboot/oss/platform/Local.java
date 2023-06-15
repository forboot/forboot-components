/*
 * 爱组搭 http://aizuda.com 低代码组件化开发平台
 * ------------------------------------------
 * 受知识产权保护，请勿删除版权申明
 */
package com.forboot.oss.platform;


import com.forboot.oss.AbstractFileStorage;
import com.forboot.oss.MultipartUploadResponse;
import com.forboot.oss.model.response.OssResult;
import com.forboot.oss.property.OssProperty;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 本地存储
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
public class Local extends AbstractFileStorage {

    public Local(OssProperty ossProperty) {
        this.ossProperty = ossProperty;
    }

    @Override
    public OssResult upload(InputStream is, String filename, String objectName) throws Exception {
        String suffix = this.getFileSuffix(filename);
        String _objectName = this.getObjectName(suffix, objectName);
        File file = new File(this.getLocalFilePath(_objectName));
        if (!file.exists()) {
            // 文件不存在则创建文件，先创建目录
            File dir = new File(file.getParent());
            dir.mkdirs();
            file.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IoUtils.write(is, fos);
        }
        return OssResult.builder().bucketName(this.ossProperty.getLocalFileUrl())
                .objectName(_objectName)
                .filename(filename)
                .suffix(suffix)
                .build();
    }

    protected String getLocalFilePath(String objectName) throws FileNotFoundException {
        return this.ossProperty.getLocalFilePath(objectName);
    }

    @Override
    public InputStream download(String objectName) throws Exception {
        try (FileInputStream fis = new FileInputStream(this.getObjectFile(objectName))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IoUtils.write(fis, out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Override
    public boolean delete(List<String> objectNameList) throws Exception {
        if (ObjectUtils.isEmpty(objectNameList)) {
            return false;
        }
        for (String objectName : objectNameList) {
            this.delete(objectName);
        }
        return true;
    }

    @Override
    public boolean delete(String objectName) throws Exception {
        File file = this.getObjectFile(objectName);
        if (null == file || !file.exists()) {
            return false;
        }
        return file.delete();
    }

    protected File getObjectFile(String objectName) throws FileNotFoundException {
        return new File(this.getLocalFilePath(objectName));
    }

    @Override
    public String getUrl(String objectName, int duration, TimeUnit unit) throws Exception {
        return objectName;
    }

    @Override
    public MultipartUploadResponse getUploadSignedUrl(String filename) {
        return null;
    }
}
