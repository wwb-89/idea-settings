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