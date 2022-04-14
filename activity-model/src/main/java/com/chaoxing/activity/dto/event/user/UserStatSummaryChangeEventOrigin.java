package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**用户活动统计改变事件源
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-29 16:07:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatSummaryChangeEventOrigin extends AbstractEventOrigin {

    /** 用户id */
    private Integer uid;
    /** 活动id */
    private Integer activityId;
    /** 时间戳 */
    private Long timestamp;

}