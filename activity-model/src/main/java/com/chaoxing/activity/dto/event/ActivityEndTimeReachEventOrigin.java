package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**活动结束时间到达事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityEndTimeReachEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-26 20:01:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEndTimeReachEventOrigin extends AbstractEventOrigin {

    private Integer activityId;
    private LocalDateTime endTime;
    private Long timestamp;

}