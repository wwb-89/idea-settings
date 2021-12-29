package com.chaoxing.activity.vo;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2021/11/30 6:06 下午
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityVO {

    /** 活动id */
    private Integer id;
    /** 活动名称 */
    private String name;
    /** 活动封面云盘id */
    private String coverCloudId;
    /** 活动封面地址 */
    private String coverUrl;
    /** 活动开始时间yyyy-MM-dd HH:mm:ss */
    private String startTime;
    /** 活动结束时间yyyy-MM-dd HH:mm:ss */
    private String endTime;
    /** 活动状态 */
    private Integer status;
    /** 是否发布 */
    private Boolean released;
    /** 是否归档 */
    private Boolean archived;
    /** 预览页面url */
    private String previewUrl;
    /** 配置页面url */
    private String editUrl;
    /** 课程id */
    private Integer courseId;
    /** 班级id */
    private Integer clazzId;
    /** 活动创建者uid */
    private Integer createUid;
    /** 活动创建者 */
    private String createUsername;
    /** 活动创建机构fid */
    private Integer createFid;
    /** 活动创建机构 */
    private String createOrgName;

    /** 活动简介 */
    private String introduction;
    /** 活动时间范围 */
    private String timeScope;

    /**
     * 活动list转换为活动vo list
     * @Description
     * @author huxiaolong
     * @Date 2021-12-01 10:55:27
     * @param activities
     * @return
     */
    public static List<ActivityVO> activitiesConvert2Vo(List<Activity> activities) {
        List<ActivityVO> newActivities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(activities)) {
            activities.forEach(v -> newActivities.add(ActivityVO.activityConvert2Vo(v)));
        }
        return newActivities;
    }

    public static ActivityVO activityConvert2Vo(Activity activity) {
        return ActivityVO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .coverCloudId(activity.getCoverCloudId())
                .coverUrl(activity.getCoverUrl())
                .startTime(activity.getStartTime() == null ? null : activity.getStartTime().format(DateUtils.FULL_TIME_FORMATTER))
                .endTime(activity.getEndTime() == null ? null : activity.getEndTime().format(DateUtils.FULL_TIME_FORMATTER))
                .status(activity.getStatus())
                .released(activity.getReleased())
                .archived(activity.getArchived())
                .previewUrl(activity.getPreviewUrl())
                .editUrl(activity.getEditUrl())
                .courseId(activity.getCourseId())
                .clazzId(activity.getClazzId())
                .createUid(activity.getCreateUid())
                .createUsername(activity.getCreateUserName())
                .createFid(activity.getCreateFid())
                .createOrgName(activity.getCreateOrgName())
                .build();
    }
}
