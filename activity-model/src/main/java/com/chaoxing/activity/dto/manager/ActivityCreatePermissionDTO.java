package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.model.Classify;
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

    /** 微服务发布范围不受限 */
    private Boolean wfwReleaseScopeNoLimit;

    /** 通讯录发布范围不受限 */
    private Boolean contactsReleaseScopeNoLimit;

    /**
     * 组织架构
     */
    private List<WfwGroupDTO> wfwGroups;
    /**
     * 组织架构
     */
    private List<WfwGroupDTO> contactsGroups;

    /**
     * 活动类型
     */
    private List<Classify> classifies;

    public static ActivityCreatePermissionDTO buildDefault() {
        return ActivityCreatePermissionDTO.builder()
                .wfwReleaseScopeNoLimit(Boolean.FALSE)
                .contactsReleaseScopeNoLimit(Boolean.FALSE)
                .wfwGroups(Lists.newArrayList())
                .contactsGroups(Lists.newArrayList())
                .classifies(Lists.newArrayList())
                .build();
    }
}
