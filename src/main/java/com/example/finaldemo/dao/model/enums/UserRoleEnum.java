package com.example.finaldemo.dao.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER("用户", "user", 0),
    SUPER("会员", "super", 1),
    ADMIN("管理员", "admin", 2);

    private final String text;

    private final String value;

    private final int level;

    UserRoleEnum(String text, String value, int level) {
        this.text = text;
        this.value = value;
        this.level = level;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
