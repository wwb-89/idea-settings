package com.chaoxing.activity.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**通知模版字段对象
 * @author wwb
 * @version ver 1.0
 * @className NoticeTemplateFieldDTO
 * @description
 * @blame wwb
 * @date 2021-11-16 17:23:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeTemplateFieldDTO {

    private String activityName;
    private String address;
    private String activityTime;
    private String previewUrl;
    // 黑名单所需字段
    /** 活动主办方 */
    private String activityOrganisers;
    /** 未签到/签退次数 */
    private Integer notSignInOutNum;
    /** 黑名单添加时间 */
    private String blacklistAddTime;
    /** 黑名单移除时间 */
    private String blacklistRemoveTime;
    /** 黑名单规则是否自动移除 */
    private Boolean enableAutoRemove;
    /** 黑名单自动移除时间 */
    private Integer autoRemoveHours;
    // 黑名单所需字段
    private List<SignUpNoticeTemplateFieldDTO> signUps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpNoticeTemplateFieldDTO {

        private String name;
        private String time;

    }

}