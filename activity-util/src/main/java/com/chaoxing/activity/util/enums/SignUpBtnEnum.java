package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**报名按钮枚举
 * @author wwb
 * @version ver 1.0
 * @className SignUpBtnEnum
 * @description
 * @blame wwb
 * @date 2021-11-17 15:34:00
 */
@Getter
public enum SignUpBtnEnum {

    /** 报名参与 */
    BTN_1("报名参与", "报名"),
    BTN_2("立即报名", "报名"),
    BTN_3("立即预约", "预约"),
    BTN_4("立即申请", "申请");

    private final String name;
    private final String keyword;

    SignUpBtnEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
    }

}