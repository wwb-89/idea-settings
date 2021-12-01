package com.chaoxing.activity.admin.vo;

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
            activities.forEach(v -> {
                newActivities.add(ActivityVO.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .coverCloudId(v.getCoverCloudId())
                        .coverUrl(v.getCoverUrl())
                        .startTime(v.getStartTime() == null ? null : v.getStartTime().format(DateUtils.FULL_TIME_FORMATTER))
                        .endTime(v.getEndTime() == null ? null : v.getEndTime().format(DateUtils.FULL_TIME_FORMATTER))
                        .status(v.getStatus())
                        .build());
            });
        }
        return newActivities;
    }
}
