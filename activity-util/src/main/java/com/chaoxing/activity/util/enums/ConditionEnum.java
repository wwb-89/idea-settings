package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**报名条件枚举
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/11/2 17:02
 * <p>
 */
@Getter
public enum ConditionEnum {
    /** 不限 */
    NO_LIMIT("不限", ""),
    /** 等于 */
    EQUALS("等于", "="),
    /** 不等于 */
    UN_EQUALS("不等于", "!="),
    /** 包含 */
    INCLUDE("包含", "include"),
    /** 不包含 */
    EXCLUDE("不包含", "exclude"),
    /** 为空 */
    EMPTY("为空", "empty"),
    /** 不为空 */
    UN_EMPTY("不为空", "un_empty"),
    /** 大于 */
    GREATER_THAN("大于", ">"),
    /** 小于 */
    LESS_THAN("小于", "<"),
    /** 大于等于 */
    GREATER_THAN_OR_EQUALS("大于等于", ">="),
    /** 小于等于 */
    LESS_THEN_OR_EQUALS("小于等于", "<=");


    private final String name;

    private final String value;

    ConditionEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ConditionEnum fromValue(String value) {
        for (ConditionEnum itemEnum : ConditionEnum.values()) {
            if (Objects.equals(itemEnum.getValue(), value)) {
                return itemEnum;
            }
        }
        return null;
    }
}
