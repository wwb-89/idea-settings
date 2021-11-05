package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动门户模版改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityWebTemplateChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-27 18:44:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityWebTemplateChangeEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 时间戳 */
    private Long timestamp;

}
