package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**活动时间修改数据源
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimeChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:11:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTimeChangeEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    private LocalDateTime oldStartTime;
    private LocalDateTime newStartTime;
    private LocalDateTime oldEndTime;
    private LocalDateTime newEndTime;
    /** 时间戳 */
    private Long timestamp;

}