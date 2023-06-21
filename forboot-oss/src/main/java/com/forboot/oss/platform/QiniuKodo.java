package com.forboot.oss.platform;

import com.forboot.oss.AbstractFileStorage;
import com.forboot.oss.MultipartUploadResponse;
import com.forboot.oss.enums.AccessControl;
import com.forboot.oss.model.response.OssResult;
import com.forboot.oss.property.OssProperty;
import com.forboot.toolkit.StrUtils;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.*;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 七牛云 存储
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/21
 */
@Slf4j
public class QiniuKodo extends AbstractFileStorage {

    private final UploadManager uploadManager;

    private final BucketManager bucketManager;

    private final Auth auth;

    private final String upToken;

    public QiniuKodo(OssProperty ossProperty) {
        this.ossProperty = ossProperty;

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        // 指定分片上传版本
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        uploadManager = new UploadManager(cfg);
        auth = Auth.create(ossProperty.getAccessKey(), ossProperty.getSecretKey());
        bucketManager = new BucketManager(auth, cfg);
        upToken = auth.uploadToken(ossProperty.getBucketName());
    }

    /**
     * 上传
     *
     * @param is         文件流 {@link InputStream}
     * @param filename   文件名
     * @param objectName 文件对象名
     * @return {@link OssResult}
     */
    @Override
    public OssResult uploadFile(InputStream is, String filename, String objectName) throws Exception {
        String bucketName = this.getBucketName();
        String suffix = this.getFileSuffix(filename);
        String fileName = this.getObjectName(suffix, objectName);

        Response response = uploadManager.put(is.readAllBytes(), fileName, upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return OssResult.builder().bucketName(bucketName)
                        .objectName(putRet.key)
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
        String oldFileName = fileName;
        // 如果 fileName 前缀有 / 则去掉
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        Response response = uploadManager.put(is.readAllBytes(), fileName, upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return null == putRet ? null : OssResult.builder().bucketName(bucketName)
                .objectName(oldFileName)
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
        String oldFileName = fileName;
        // 如果 fileName 前缀有 / 则去掉
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        Response response = uploadManager.put(is.readAllBytes(), fileName, upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return null == putRet ? null : OssResult.builder().bucketName(bucketName)
                .objectName(oldFileName)
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
        String bucketName = this.getBucketName();
        InputStream is = file.getInputStream();
        fileName = dir.concat("/").concat(fileName);
        String oldFileName = fileName;
        // 如果 fileName 前缀有 / 则去掉
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        Response response = uploadManager.put(is.readAllBytes(), fileName, upToken);
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return null == putRet ? null : OssResult.builder().bucketName(bucketName)
                .objectName(oldFileName)
                .filename(file.getOriginalFilename())
                .suffix(suffix)
                .build();
    }

    /**
     * 下载
     *
     * @param objectName 文件对象名
     * @return {@link InputStream}
     */
    @Override
    public InputStream download(String objectName) throws Exception {
        String bucketDomain = this.getBucketDomain();
        // 判断是否有为https
        boolean isHttps = bucketDomain.startsWith("https://");
        DownloadUrl downloadUrl = new DownloadUrl(bucketDomain, isHttps, objectName);
        // 设置附件名称 不设置，则该链接不会弹出下载框
//        downloadUrl.setAttname("rose.jpg");
        // 设置数据处理操作
//        downloadUrl.setFop("imageMogr2/thumbnail/!50p");
        // 设置样式名称
//        downloadUrl.setStyle("small", "_", "?imageView2/2/w/100/h/100/format/jpg/interlace/1/q/75|imageslim");

        String url = null;

        if (AccessControl.PUBLIC.equalsCode(ossProperty.getAccessControl())) {
            url = downloadUrl.buildURL();
        } else if (AccessControl.PRIVATE.equalsCode(ossProperty.getAccessControl())) {
            // 1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            long deadline = System.currentTimeMillis() / 1000 + expireInSeconds;
            url = downloadUrl.buildURL(auth, deadline);
        }
        if (StrUtils.isBlank(url)) {
            throw new Exception("download url is null");
        }
        // 将下载链接转为输入流
        return new URL(url).openStream();
    }

    /**
     * 删除文件
     *
     * @param objectNameList 文件对象名列表
     * @throws Exception 异常
     */
    @Override
    public boolean delete(List<String> objectNameList) throws Exception {
        if (ObjectUtils.isEmpty(objectNameList)) {
            return false;
        }
        // 构建批量删除的请求参数
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        // objectNameList 转为 String 数组
        String[] objects = objectNameList.toArray(new String[0]);
        batchOperations.addDeleteOp(this.getBucketName(), objects);
        // 调用batch方法批量执行操作
        Response res = bucketManager.batch(batchOperations);
        BatchStatus[] batchStatusList = res.jsonToObject(BatchStatus[].class);
        for (int i = 0; i < objectNameList.size(); i++) {
            BatchStatus status = batchStatusList[i];
            String objectName = objectNameList.get(i);
            if (200 == status.code) {
                log.info("文件删除成功：{}", objectName);
            } else {
                log.info("文件删除失败：{}", objectName);
            }
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param objectName 文件对象名
     * @throws Exception 异常
     */
    @Override
    public boolean delete(String objectName) throws Exception {
        if (StrUtils.isBlank(objectName)) {
            return false;
        }
        bucketManager.delete(this.getBucketName(), objectName);
        return true;
    }

    /**
     * 获取文件地址
     *
     * @param objectName 文件对象名
     * @param duration   期间
     * @param unit       时间单位 {@link TimeUnit}
     * @return 文件地址
     */
    @Override
    public String getUrl(String objectName, int duration, TimeUnit unit) throws Exception {
        String bucketDomain = this.getBucketDomain();
        // 判断是否有为https
        boolean isHttps = bucketDomain.startsWith("https://");
        DownloadUrl downloadUrl = new DownloadUrl(bucketDomain, isHttps, objectName);
        // 设置附件名称 不设置，则该链接不会弹出下载框
//        downloadUrl.setAttname("rose.jpg");
        // 设置数据处理操作
//        downloadUrl.setFop("imageMogr2/thumbnail/!50p");
        // 设置样式名称
//        downloadUrl.setStyle("small", "_", "?imageView2/2/w/100/h/100/format/jpg/interlace/1/q/75|imageslim");

        String url = null;

        if (AccessControl.PUBLIC.equalsCode(ossProperty.getAccessControl())) {
            url = downloadUrl.buildURL();
        } else if (AccessControl.PRIVATE.equalsCode(ossProperty.getAccessControl())) {
            // 1小时，可以自定义链接过期时间
            long expireInSeconds = 3600;
            long deadline = System.currentTimeMillis() / 1000 + expireInSeconds;
            url = downloadUrl.buildURL(auth, deadline);
        }
        if (StrUtils.isBlank(url)) {
            throw new Exception("get file url is error");
        }
        return url;
    }

    /**
     * 获取上传签名地址
     *
     * @param filename 文件名
     * @return {@link MultipartUploadResponse}
     */
    @Override
    public MultipartUploadResponse getUploadSignedUrl(String filename) {
        return null;
    }
}
