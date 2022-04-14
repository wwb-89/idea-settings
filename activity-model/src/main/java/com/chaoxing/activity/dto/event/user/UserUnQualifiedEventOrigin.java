package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户不合格事件源
 * @author wwb
 * @version ver 1.0
 * @className UserUnQualifiedEventOrigin
 * @description
 * @blame wwb
 * @date 2021-11-01 10:38:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUnQualifiedEventOrigin extends AbstractEventOrigin {

    private Integer activityId;
    private Integer uid;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}
