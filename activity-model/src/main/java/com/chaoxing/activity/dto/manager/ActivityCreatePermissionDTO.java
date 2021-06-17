package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.model.ActivityClassify;
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

    /** 是否存在不限的发布范围角色权限 */
    private Boolean existNoLimitPermission;
    /**
     * 组织架构
     */
    private List<WfwGroupDTO> wfwGroups;

    /**
     * 活动类型
     */
    private List<ActivityClassify> activityClassifies;

    public static ActivityCreatePermissionDTO buildDefault() {
        return ActivityCreatePermissionDTO.builder()
                .existNoLimitPermission(Boolean.TRUE)
                .wfwGroups(Lists.newArrayList())
                .activityClassifies(Lists.newArrayList())
                .build();
    }
}
