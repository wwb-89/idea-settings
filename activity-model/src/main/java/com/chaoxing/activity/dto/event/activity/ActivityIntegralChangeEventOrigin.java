package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动积分改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityIntegralChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-26 16:35:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityIntegralChangeEventOrigin extends AbstractEventOrigin {
    
    /** 活动id */
    private Integer activityId;
    /** 时间戳 */
    private Long timestamp;

}
