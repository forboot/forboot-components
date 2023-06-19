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
import org.springframework.web.multipart.MultipartFile;

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
    public OssResult uploadFile(InputStream is, String filename, String objectName) throws Exception {
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

    /**
     * 上传
     *
     * @param file 文件
     * @return {@link OssResult} 上传结果
     * @throws Exception 异常
     */
    @Override
    public OssResult uploadFile(MultipartFile file) throws Exception {
        String suffix = this.getSuffix(file.getContentType());
        InputStream is = file.getInputStream();
        String fileName = this.getObjectName(suffix);
        Path path = Paths.get(this.getLocalFilePath(fileName));
        Files.createDirectories(path.getParent());
        Files.copy(is, path);
        return OssResult.builder().bucketName(this.ossProperty.getLocalFileUrl())
                .objectName(fileName)
                .filename(file.getOriginalFilename())
                .suffix(suffix)
                .build();
    }

    /**
     * 上传
     *
     * @param file 文件
     * @param dir  想要上传到的文件目录
     * @return {@link OssResult} 上传结果
     * @throws Exception 异常
     */
    @Override
    public OssResult uploadFile(MultipartFile file, String dir) throws Exception {
        String suffix = this.getSuffix(file.getContentType());
        InputStream is = file.getInputStream();
        String fileName = dir.concat("/").concat(this.getObjectName(suffix));
        Path path = Paths.get(this.getLocalFilePath(fileName));
        Files.createDirectories(path.getParent());
        Files.copy(is, path);
        return OssResult.builder().bucketName(this.ossProperty.getLocalFileUrl())
                .objectName(fileName)
                .filename(file.getOriginalFilename())
                .suffix(suffix)
                .build();
    }

    /**
     * 上传
     *
     * @param file     文件
     * @param dir      想要上传到的文件目录
     * @param fileName 想要上传的文件名
     * @return {@link OssResult} 上传结果
     * @throws Exception 异常
     */
    @Override
    public OssResult uploadFile(MultipartFile file, String dir, String fileName) throws Exception {
        String suffix = this.getSuffix(file.getContentType());
        InputStream is = file.getInputStream();
        fileName = dir.concat("/").concat(fileName);
        Path path = Paths.get(this.getLocalFilePath(fileName));
        Files.createDirectories(path.getParent());
        Files.copy(is, path);
        return OssResult.builder().bucketName(this.ossProperty.getLocalFileUrl())
                .objectName(fileName)
                .filename(file.getOriginalFilename())
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
        return this.getLocalFilePath(objectName);
    }

    @Override
    public MultipartUploadResponse getUploadSignedUrl(String filename) {
        return null;
    }
}
