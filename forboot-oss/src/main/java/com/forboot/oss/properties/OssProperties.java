package com.forboot.oss.properties;

import com.forboot.oss.property.OssProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>OSS 存储配置属性
 *
 * @author ye21st
 * @since 2023/6/15
 */
@Setter
@Getter
@ConfigurationProperties(prefix = OssProperties.OSS_PREFIX)
public class OssProperties {

    public static final String OSS_PREFIX = "forboot";

    public static String DEFAULT_PLATFORM;

    /**
     * 获取默认存储平台
     * @return 默认存储平台
     */
    public static String getDefaultPlatform() {
        return DEFAULT_PLATFORM;
    }

    /**
     * OSS 配置属性
     */
    private Map<String, OssProperty> oss = new LinkedHashMap<>();

}
