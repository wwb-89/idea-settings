package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动名称、事件修改事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameTimeChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-27 15:36:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityNameTimeChangeEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}