package com.forboot.oss;

import com.forboot.oss.properties.OssProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件存储接口
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Slf4j
public class Oss {

    /**
     * 根据平台选择文件存储实现实例
     *
     * @param platform 存储平台，对应 yml 配置 map key
     * @return 文件存储实现实例 {@link IFileStorage}
     */
    public static IFileStorage fileStorage(String platform) {
        return SpringUtils.getBean(platform, IFileStorage.class);
    }

    /**
     * 根据平台选择文件存储实现实例
     *
     * @return 文件存储实现实例 {@link IFileStorage}
     */
    public static IFileStorage fileStorage() {
        return SpringUtils.getBean(OssProperties.getDefaultPlatform(), IFileStorage.class);
    }
}
