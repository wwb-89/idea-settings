package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/13 15:33
 * <p>
 */
@Getter
public enum MhAppIconEnum {
    ;

    @Getter
    public enum ONE {

        /** 收藏 */
        COLLECTED("collected.png", "fd55f9794e52c4125b9c885e4b8297c8"),
        /** 签到数 */
        SIGNED_IN_NUM("signed-in-num.png", "efed0911bda0f36389a9a3a168856502"),
        /** 时间 */
        TIME("time.png", "9a08c693ef2457b6a26169603cea851d"),
        /** 统计-彩色 */
        STATISTICS_COLOR("statistics-color.png", "26352e7dcdbbd10b695dbcc015c19ba2"),
        /** 海报 */
        POSTER("poster.png", "b22c13be2b47131378779c8a636fe173"),
        /** 已报名用户 */
        SIGNED_UP_USER("signed-up-user.png", "8b2229560d8df09a5c216b2ad38de9f3"),
        /** 积分 */
        INTEGRAL("integral.png", "f71b7862035605bbd91a3ab8bf3dbcc4"),
        /** 时间-透明 */
        TIME_TRANSPARENT("time-transparent.png", "339f5085fb968439dd0baaaa3a026aa9"),
        /** 活动地点 */
        LOCATION("location.png", "c9f7ae53e8fbdc5a32ad336f5871fbde"),
        /** 主办方 */
        ORGANISER("organiser.png", "b8aa41af62d0dc9e2e873b13ebcf4949"),
        /** 报名数 */
        SIGNED_UP_NUM("signed-up-num.png", "92be6e1b9bfe3c46da5e13b9702bcc21"),
        /** 评价 */
        RATING("rating.png", "c840a53bfc108db42b80710af8567465");

        private final String name;
        private final String value;

        ONE(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

}

