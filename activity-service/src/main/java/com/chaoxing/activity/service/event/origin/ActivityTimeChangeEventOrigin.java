package com.chaoxing.activity.service.event.origin;

import java.time.LocalDateTime;

/**活动时间修改数据源
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimeChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:11:18
 */
public class ActivityTimeChangeEventOrigin extends ActivityChangeEventOrigin {

    private LocalDateTime oldStartTime;
    private LocalDateTime newStartTime;
    private LocalDateTime oldEndTime;
    private LocalDateTime newEndTime;

}