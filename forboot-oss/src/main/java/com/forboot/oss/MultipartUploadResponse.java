package com.forboot.oss;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件上传返回对象
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Setter
@Getter
@Builder
public class MultipartUploadResponse {
    /**
     * 存储桶名
     */
    private String bucketName;
    /**
     * 对象名
     */
    private String objectName;
    /**
     * 上传地址
     */
    private String uploadUrl;

}
