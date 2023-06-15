package com.forboot.oss.autoconfigure;

import com.forboot.oss.properties.OssProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: OssAutoConfiguration
 * @Description: forboot oss 自动装配
 * @Author: ye21st
 * @Date: 2023/6/15
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {
}
