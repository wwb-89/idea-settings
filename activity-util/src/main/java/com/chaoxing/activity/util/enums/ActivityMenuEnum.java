package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**活动菜单
 * @author wwb
 * @version ver 1.0
 * @className ActivityMenuEnum
 * @description
 * @blame wwb
 * @date 2021-08-06 16:03:24
 */
@Getter
public enum ActivityMenuEnum {

    /** "报名名单" */
    SIGN_UP("报名名单", "sign_up", "管理报名名单、报名审核等功能", 1),
    SIGN_IN("签到管理", "sign_in", "管理签到、发布签到", 10),
    RESULTS_MANAGE("考核管理", "results_manage", "审核用户参与活动的成绩是否为合格", 20),
    CERTIFICATE("证书发放", "certificate", "", 30),
    NOTICE("发通知", "notice", "", 40),
    STAT("统计", "stat", "", 50),
    SETTING("设置", "setting", "", 500);

    private final String name;
    private final String value;
    private final String desc;
    private final Integer sequence;

    ActivityMenuEnum(String name, String value, String desc, Integer sequence) {
        this.name = name;
        this.value = value;
        this.desc = desc;
        this.sequence = sequence;
    }

    public static ActivityMenuEnum fromValue(String value) {
        ActivityMenuEnum[] values = ActivityMenuEnum.values();
        for (ActivityMenuEnum activityMenuEnum : values) {
            if (Objects.equals(activityMenuEnum.getValue(), value)) {
                return activityMenuEnum;
            }
        }
        return null;
    }

}