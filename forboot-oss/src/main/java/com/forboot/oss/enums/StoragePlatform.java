package com.forboot.oss.enums;

import com.forboot.oss.platform.*;
import lombok.Getter;

/**
 * oss 文件存储平台
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Getter
public enum StoragePlatform {

    /**
     * minio
     */
    MINIO(Minio.class),

    /**
     * 阿里云
     */
    ALIYUN(AliyunOss.class),

    /**
     * 腾讯云
     */
    TENCENT_COS(TencentCos.class),

    /**
     * 亚马逊
     */
    AWS_S3(AwsS3.class),

    /**
     * 本地存储
     */
    LOCAL(Local.class),

    /**
     * 七牛云
     */
    QINIU_KODO(QiniuKodo.class),
    ;

    private final Class<?> strategyClass;

    StoragePlatform(Class<?> strategyClass) {
        this.strategyClass = strategyClass;
    }

}
