package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/3 14:16
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCollectionDTO {

    /** 活动id */
    private Integer activityId;
    /** 收藏数 */
    private Integer collectedNum;
}
