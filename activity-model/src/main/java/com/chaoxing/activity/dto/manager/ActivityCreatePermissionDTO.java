package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.ActivityStatSummary;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/3 4:13 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreatePermissionDTO {

    /**
     * 组织架构
     */
    List<WfwGroupDTO> wfwGroups;

    /**
     * 活动类型
     */
    List<ActivityClassify> activityClassifies;

    public static ActivityCreatePermissionDTO buildDefault() {
        return ActivityCreatePermissionDTO.builder()
                .wfwGroups(Lists.newArrayList())
                .activityClassifies(Lists.newArrayList())
                .build();
    }
}
