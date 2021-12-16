package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户证书发放事件来源
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueEventOrigin
 * @description
 * @blame wwb
 * @date 2021-12-16 15:46:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCertificateIssueEventOrigin extends AbstractEventOrigin {

    /** 用户id */
    private Integer uid;
    /** 活动id */
    private Integer activityId;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}