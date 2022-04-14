package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**万能表单关联活动回写数据对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityWriteBackDataDTO
 * @description
 * @blame wwb
 * @date 2021-11-22 18:27:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormActivityWriteBackDataDTO {

    /** 活动id */
    private Integer activityId;
    /** 活动状态 */
    private String activityStatus;
    /** 发布状态 */
    private String activityReleaseStatus;
    /** 报名状态 */
    private String signUpStatus;
    /** 发布状态 */
    private String previewUrl;

}
