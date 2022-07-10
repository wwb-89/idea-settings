package com.chaoxing.activity.dto.event.activity;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动名称修改事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityNameChangedEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:10:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityNameChangedEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 旧的名称 */
    private String oldName;
    /** 新的名称 */
    private String newName;
    /** 时间戳 */
    private Long timestamp;

}