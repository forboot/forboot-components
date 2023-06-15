package com.forboot.oss.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 存储返回结果对象
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Getter
@Setter
@Builder
public class OssResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -6877089821605243850L;

    /**
     * 存储桶名
     */
    private String bucketName;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 版本
     */
    private String versionId;

}
