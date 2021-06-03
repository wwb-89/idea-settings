package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrderTypeEnum
 * @description
 * @blame wwb
 * @date 2021-05-28 15:50:07
 */
@Getter
public enum OrderTypeEnum {

    ASC("正序", "asc"),
    DESC("倒序", "desc");

    private final String name;
    private final String value;

    OrderTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static OrderTypeEnum fromValue(String value) {
        OrderTypeEnum[] values = OrderTypeEnum.values();
        for (OrderTypeEnum orderTypeEnum : values) {
            if (Objects.equals(orderTypeEnum.getValue(), value)) {
                return orderTypeEnum;
            }
        }
        return null;
    }

}
