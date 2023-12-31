package com.forboot.oss.property;

import com.forboot.oss.enums.StoragePlatform;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * oss 存储配置属性
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Getter
@Setter
public class OssProperty {

    /**
     * 存储平台
     */
    private StoragePlatform platform;

    /**
     * 域名
     */
    private String endpoint;

    /**
     * ACCESS_KEY
     */
    private String accessKey;

    /**
     * SECRET_KEY
     */
    private String secretKey;

    /**
     * 存储空间名
     */
    private String bucketName;

    /**
     * 存储空间域名
     */
    private String bucketDomain;

    /**
     * 连接超时时间，默认设置一分钟
     */
    private int connectionTimeout = 60000;

    /**
     * 文件服务器域名
     */
    private String localFileUrl;

    /**
     * 文件存储路径
     */
    private String localFilePath;

    /**
     * 允许上传媒体类型（不设置默认所有文件）
     * <p>
     * 例如：PNG图片 image/png
     * <br/>
     * 文件类型对照表：https://tool.oschina.net/commons
     * </p>
     */
    private List<String> allowMediaType;

    /**
     * 访问授权 公用空间/私有空间
     */
    private String accessControl;

    public String getLocalFilePath(String objectName) throws FileNotFoundException {
        if (null == this.localFilePath) {
            throw new FileNotFoundException("localFilePath is Empty");
        }
        return this.localFilePath + "/" + objectName;
    }
}
