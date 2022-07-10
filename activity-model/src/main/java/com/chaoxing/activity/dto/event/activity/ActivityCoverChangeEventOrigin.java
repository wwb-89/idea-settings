package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动封面改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-26 18:19:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCoverChangeEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}
