package com.forboot.oss;

import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;

/**
 * oss 媒体类型辅助类
 * <p>
 * 尊重知识产权，CV 请保留版权，<a href="https://www.forboot.com">ForBoot</a> 出品
 *
 * @author ye21st
 * @since 2023/6/15
 */
public class MediaType {
    private static Tika TIKA;

    public static Tika getTika() {
        return TIKA;
    }

    /**
     * 提取媒体类型
     *
     * @param is 文件流 {@link InputStream}
     * @return 媒体类型
     * @throws IOException IO 异常
     */
    public static String detect(InputStream is) throws IOException {
        if (null == TIKA) {
            synchronized (MediaType.class) {
                TIKA = new Tika();
            }
        }
        return TIKA.detect(is);
    }
}
