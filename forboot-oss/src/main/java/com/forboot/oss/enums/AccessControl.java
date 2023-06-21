package com.forboot.oss.enums;

import com.forboot.toolkit.StrUtils;

/**
 * @ClassName: AccessControl
 * @Description: 访问控制，公有空间/私有空间
 * @Author: ye21st
 * @Date: 2023/6/21
 */
public enum AccessControl {

    /**
     * 公有空间
     */
    PUBLIC,

    /**
     * 私有空间
     */
    PRIVATE;

    public String getCode() {
        return this.name().toLowerCase();
    }

    public boolean equalsCode(String code) {
        if (StrUtils.isBlank(code)) {
            return false;
        }
        return this.getCode().equalsIgnoreCase(code);
    }
}
