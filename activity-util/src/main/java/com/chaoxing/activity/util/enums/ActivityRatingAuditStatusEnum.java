package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**
 * @author xhl
 * @version ver 1.0
 * @className ActivityRatingAuditStatusEnum
 * @description
 * @blame xhl
 * @date 2021-03-11 20:47:04
 */
@Getter
public enum ActivityRatingAuditStatusEnum {

    NOT_PASS("不通过", 0),
    PASSED("通过", 1),
    WAIT("待审核", 2);


    private String name;
    private Integer value;

    ActivityRatingAuditStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
