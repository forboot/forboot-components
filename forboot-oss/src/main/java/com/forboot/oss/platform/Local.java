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
import com.forboot.toolkit.IoUtils;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String fileName = this.getObjectName(suffix, objectName);
        Path path = Paths.get(this.getLocalFilePath(fileName));
        Files.createDirectories(path.getParent());
        Files.copy(is, path);
        return OssResult.builder().bucketName(this.ossProperty.getLocalFileUrl())
                .objectName(fileName)
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
