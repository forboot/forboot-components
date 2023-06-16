package com.forboot.oss.autoconfigure;

import com.forboot.oss.IFileStorage;
import com.forboot.oss.properties.OssProperties;
import com.forboot.oss.property.OssProperty;
import com.forboot.toolkit.ObjectUtils;
import com.forboot.toolkit.SpringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @ClassName: OssAutoConfiguration
 * @Description: forboot oss 自动装配
 * @Author: ye21st
 * @Date: 2023/6/15
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    private final OssProperties ossProperties;

    public OssAutoConfiguration(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    public IFileStorage fileStorage(ApplicationContext applicationContext) {
        Map<String, OssProperty> oss = ossProperties.getOss();
        if (ObjectUtils.isEmpty(oss)) {
            throw new BeanInitializationException("oss init error");
        }
        SpringUtils.setApplicationContext(applicationContext);
        oss.forEach((k, v) -> {
            try {
                if (null == OssProperties.DEFAULT_PLATFORM) {
                    // 第一个配置为默认存储
                    OssProperties.DEFAULT_PLATFORM = k;
                }
                Class clazz = v.getPlatform().getStrategyClass();
                Constructor constructor = clazz.getConstructor(OssProperty.class);
                SpringUtils.registerSingletonBean(k, constructor.newInstance(v));
            } catch (Exception e) {
                throw new BeanInitializationException("register bean error", e);
            }
        });
        return SpringUtils.getBean(OssProperties.DEFAULT_PLATFORM, IFileStorage.class);
    }

}
