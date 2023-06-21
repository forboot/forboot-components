package com.forboot.oss;


import com.forboot.oss.exception.MediaTypeException;
import com.forboot.oss.property.OssProperty;
import com.forboot.toolkit.DateUtils;
import com.forboot.toolkit.ThreadLocalUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * 抽象文件存储类
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
public abstract class AbstractFileStorage implements IFileStorage {
    /**
     * 配置属性
     */
    protected OssProperty ossProperty;

    /**
     * 存储桶名称
     */
    protected String getBucketName() {
        String tempBucketName = this.tempBucketName();
        String bucketName = ThreadLocalUtils.get(tempBucketName);
        if (null == bucketName) {
            bucketName = ossProperty.getBucketName();
        } else {
            // 释放临时桶名称
            ThreadLocalUtils.remove(tempBucketName);
        }
        return bucketName;
    }

    /**
     * 临时桶名称
     */
    protected String tempBucketName() {
        return this.getClass().getName() + "OssBucket";
    }

    @Override
    public IFileStorage bucket() {
        return this;
    }

    @Override
    public IFileStorage bucket(String bucketName) {
        if (StringUtils.hasLength(bucketName)) {
            ThreadLocalUtils.set(this.tempBucketName(), bucketName);
        }
        return this;
    }

    /**
     * 存储对象名称，默认生成日期文件路径，按年月目录存储
     *
     * @param suffix     文件后缀
     * @return 文件名，包含存储路径
     */
    protected String getObjectName(String suffix) {
        return DateUtils.nowFormat("yyyy/MM/dd") + "/" + UUID.randomUUID() + "." + suffix;
    }

    /**
     * 存储对象名称，默认生成日期文件路径，按年月目录存储
     *
     * @param suffix     文件后缀
     * @param objectName 文件对象名
     * @return 文件名，包含存储路径
     */
    protected String getObjectName(String suffix, String objectName) {
        if (null != objectName) {
            return objectName;
        }
        return DateUtils.nowFormat("yyyy/MM/dd") + "/" + UUID.randomUUID() + "." + suffix;
    }

    protected String getEndpoint() {
        return ossProperty.getEndpoint();
    }

    protected String getBucketDomain() {
        return ossProperty.getBucketDomain();
    }

    @Override
    public IFileStorage allowMediaType(InputStream is, Function<String, Boolean> function) throws Exception {
        boolean legal = false;
        is.mark(is.available() + 1);
        String mediaType = MediaType.detect(is);
        if (null == function) {
            List<String> allowMediaType = this.ossProperty.getAllowMediaType();
            if (null != allowMediaType) {
                legal = allowMediaType.stream().anyMatch(mediaType::startsWith);
            }
        } else {
            legal = function.apply(mediaType);
        }
        // 不合法媒体类型抛出异常
        if (!legal) {
            throw new MediaTypeException("Illegal file type");
        }
        is.reset();
        return this;
    }
}
