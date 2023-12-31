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
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Minio 存储
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Slf4j
public class Minio extends AbstractFileStorage {

    private final MinioClient minioClient;

    public Minio(OssProperty ossProperty) {
        this.ossProperty = ossProperty;
        this.minioClient = MinioClient.builder().endpoint(ossProperty.getEndpoint())
                .credentials(ossProperty.getAccessKey(), ossProperty.getSecretKey())
                .build();
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    @Override
    public OssResult uploadFile(InputStream is, String filename, String objectName) throws Exception {
        if (null == is || null == filename) {
            return null;
        }
        String bucketName = this.getBucketName();
        String suffix = this.getFileSuffix(filename);
        String fileName = this.getObjectName(suffix, objectName);
        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName).object(fileName)
                .stream(is, is.available(), -1).build());
        return null == response ? null : OssResult.builder().bucketName(bucketName)
                .objectName(fileName)
                .versionId(response.versionId())
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
        String bucketName = this.getBucketName();
        String suffix = this.getSuffix(file.getContentType());
        InputStream is = file.getInputStream();
        String fileName = this.getObjectName(suffix);
        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName).object(fileName)
                .stream(is, is.available(), -1).build());
        return null == response ? null : OssResult.builder().bucketName(bucketName)
                .objectName(fileName)
                .versionId(response.versionId())
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
        String bucketName = this.getBucketName();
        String suffix = this.getSuffix(file.getContentType());
        InputStream is = file.getInputStream();
        String fileName = dir.concat("/").concat(this.getObjectName(suffix));
        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName).object(fileName)
                .stream(is, is.available(), -1).build());
        return null == response ? null : OssResult.builder().bucketName(bucketName)
                .objectName(fileName)
                .versionId(response.versionId())
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
        String bucketName = this.getBucketName();
        String suffix = this.getSuffix(file.getContentType());
        InputStream is = file.getInputStream();
        fileName = dir.concat("/").concat(fileName);
        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName).object(fileName)
                .stream(is, is.available(), -1).build());
        return null == response ? null : OssResult.builder().bucketName(bucketName)
                .objectName(fileName)
                .versionId(response.versionId())
                .filename(file.getOriginalFilename())
                .suffix(suffix)
                .build();
    }

    @Override
    public InputStream download(String objectName) throws Exception {
        String bucketName = this.getBucketName();
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName).object(objectName)
                .build());
    }

    @Override
    public boolean delete(List<String> objectNameList) throws Exception {
        if (ObjectUtils.isEmpty(objectNameList)) {
            return false;
        }
        String bucketName = this.getBucketName();
        minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName)
                .objects(objectNameList.stream().map(DeleteObject::new).collect(Collectors.toList()))
                .build());
        return true;
    }

    @Override
    public boolean delete(String objectName) throws Exception {
        String bucketName = this.getBucketName();
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName)
                .object(objectName).build());
        return true;
    }

    @Override
    public String getUrl(String objectName, int duration, TimeUnit unit) throws Exception {
        String bucketName = this.getBucketName();
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET).expiry(duration, unit)
                .bucket(bucketName)
                .extraQueryParams(this.queryParams(objectName))
                .object(objectName).build());
    }

    protected Map<String, String> queryParams(String filename) {
        Map<String, String> reqParams = new HashMap<>(16);
        reqParams.put("response-content-type", this.getContentType(filename));
        return reqParams;
    }

    @Override
    public MultipartUploadResponse getUploadSignedUrl(String filename) {
        try {
            String bucketName = this.getBucketName();
            String suffix = this.getFileSuffix(filename);
            String objectName = this.getObjectName(suffix, null);
            return MultipartUploadResponse.builder().bucketName(bucketName).objectName(objectName)
                    .uploadUrl(minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(12, TimeUnit.HOURS)
                            .extraQueryParams(this.queryParams(filename))
                            .build())
                    ).build();
        } catch (Exception e) {
            log.error("minio getPresignedObjectUrl error.", e);
            return null;
        }
    }
}
