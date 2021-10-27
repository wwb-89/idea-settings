package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动地点时间改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityAddressTimeChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-27 15:45:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAddressTimeChangeEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}
