package com.chaoxing.activity.api.controller.enums;

import lombok.Getter;

/**门户按钮顺序枚举
 * @author wwb
 * @version ver 1.0
 * @className MhBtnSequenceEnum
 * @description
 * @blame wwb
 * @date 2021-09-17 16:32:52
 */
@Getter
public enum MhBtnSequenceEnum {

    /** 活动相关 */
    ACTIVITY("活动相关", 1),
    SIGN_UP("报名相关", 2),
    SIGN_IN("签到相关", 3),
    WORK("作品征集相关", 4),
    GROUP("小组相关", 5),
    RATING("评价", 6),
    MANAGE("管理", 7),
    SIGN_UP_INFO("报名信息", 8);

    private final String name;
    private final Integer sequence;

    MhBtnSequenceEnum(String name, Integer sequence) {
        this.name = name;
        this.sequence = sequence;
    }

}