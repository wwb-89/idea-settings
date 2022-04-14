package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动被删除事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityDeletedEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-27 15:11:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDeletedEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}
