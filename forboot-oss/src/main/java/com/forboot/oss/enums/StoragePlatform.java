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

    MINIO(Minio.class),

    ALIYUN(AliyunOss.class),

    TENCENT_COS(TencentCos.class),

    AWS_S3(AwsS3.class),

    LOCAL(Local.class);

    private final Class strategyClass;

    StoragePlatform(Class strategyClass) {
        this.strategyClass = strategyClass;
    }

}
