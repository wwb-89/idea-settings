package com.chaoxing.activity.dto.activity.query.result;

import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**活动发布平台查询结果对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleasePlatformQueryResultDTO
 * @description
 * @blame wwb
 * @date 2021-12-02 15:48:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReleasePlatformActivityQueryResultDTO {

    /** 活动id */
    private Integer id;
    /** 活动名称 */
    private String name;
    /** 开始时间 */
    private Long startTimestamp;
    /** 结束时间 */
    private Long endTimestamp;
    /** 作品征集id */
    private Integer workId;
    /** 门户网站id */
    private Integer websiteId;
    /** 发布的机构范围 */
    private List<WfwAreaDTO> scopeOrgs;
    /** 创建人uid */
    private Integer createUid;
    /** 创建机构 */
    private Integer createFid;
    /** 是否被删除 */
    private Boolean deleted;

    public static List<ActivityReleasePlatformActivityQueryResultDTO> build(List<Activity> activities, Map<Integer, List<WfwAreaDTO>> activityIdWfwAreasMap) {
        return activities.stream().map(v -> ActivityReleasePlatformActivityQueryResultDTO.build(v, activityIdWfwAreasMap.get(v.getId()))).collect(Collectors.toList());
    }

    public static ActivityReleasePlatformActivityQueryResultDTO build(Activity activity, List<WfwAreaDTO> wfwAreas) {
        ActivityReleasePlatformActivityQueryResultDTO result = ActivityReleasePlatformActivityQueryResultDTO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .startTimestamp(DateUtils.date2Timestamp(activity.getStartTime()))
                .endTimestamp(DateUtils.date2Timestamp(activity.getEndTime()))
                .workId(activity.getWorkId())
                .websiteId(activity.getWebsiteId())
                .scopeOrgs(Optional.ofNullable(wfwAreas).orElse(Lists.newArrayList()))
                .createUid(activity.getCreateUid())
                .createFid(activity.getCreateFid())
                .deleted(Objects.equals(Activity.StatusEnum.DELETED.getValue(), activity.getStatus()))
                .build();
        return result;
    }

}