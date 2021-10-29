package com.chaoxing.activity.util.constant;

import lombok.Getter;

/**数据推送类型
 * @author wwb
 * @version ver 1.0
 * @className DataPushTypeEnum
 * @description
 * @blame wwb
 * @date 2021-10-29 17:38:34
 */
@Getter
public enum DataPushTypeEnum {

    /** 新增 */
    ADD("新增", "add"),
    MODIFY("修改", "modify"),
    DELETE("删除", "delete");

    private final String name;
    private final String value;

    DataPushTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
