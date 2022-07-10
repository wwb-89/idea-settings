package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartTimeReachEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-26 20:00:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStartTimeReachEventOrigin extends AbstractEventOrigin {

    private Integer activityId;
    private LocalDateTime startTime;
    private Long timestamp;

}
