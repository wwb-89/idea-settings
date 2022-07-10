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
    SIGN_UP("报名相关", 20),
    SIGN_IN("签到相关", 30),
    FORM_COLLECTION("表单采集相关", 31),
    WORK("作品征集相关", 40),
    READING_TEST("阅读测评", 50),
    GROUP("小组相关", 60),
    RATING("评价", 70),
    MANAGE("管理", 80),
    SIGN_UP_INFO("报名信息", 90),
    CUSTOM_APP("自定义相关", 100);

    private final String name;
    private final Integer sequence;

    MhBtnSequenceEnum(String name, Integer sequence) {
        this.name = name;
        this.sequence = sequence;
    }

}