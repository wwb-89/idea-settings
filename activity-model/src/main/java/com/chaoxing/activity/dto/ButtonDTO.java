package com.chaoxing.activity.dto;

import com.chaoxing.activity.util.constant.UrlConstant;
import lombok.*;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/20 3:32 PM
 * @version: 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ButtonDTO {

    /** 按钮名称 */
    private String name;
    /** 按钮地址 */
    private String url;
    /** 是否ajax请求 */
    private Boolean ajax;
    /** 排序 */
    private Integer sequence;



    public static ButtonDTO build(String name, String url, BtnSequenceEnum btnSequenceEnum) {
        return ButtonDTO.build(name, url, false, btnSequenceEnum);
    }

    public static ButtonDTO build(String name, String url, Boolean ajax, BtnSequenceEnum btnSequenceEnum) {
        return ButtonDTO.builder()
                .name(name)
                .url(url)
                .ajax(ajax)
                .sequence(btnSequenceEnum.getSequence()).build();
    }

    @Getter
    public enum BtnSequenceEnum {

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

        BtnSequenceEnum(String name, Integer sequence) {
            this.name = name;
            this.sequence = sequence;
        }

    }

}
