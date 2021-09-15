package com.chaoxing.activity.dto.activity.create;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;

/**来自活动发布平台活动的创建参数
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreateFromActivityReleaseParamDTO
 * @description
 * @blame wwb
 * @date 2021-09-15 15:03:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateFromActivityReleaseParamDTO {

    /** 活动发布平台活动的默认封面云盘id */
    private static final String COVER_CLOUD_ID = "4f0043ae6c5aed9277d635bfb24a2b9f";
    private static final Integer WEB_TEMPLATE_ID = 32;

    /** 源id */
    private Integer originId;
    /** 源名称 */
    private String originName;
    /** 源封面云盘id */
    private String originCoverCloudId;
    /** 源开始时间 */
    private Long originStartTime;
    /** 源结束时间 */
    private Long originEndTime;
    /** 源简介 */
    private String originIntroduction;
    /** 源创建者uid */
    private Integer originCreateUid;
    /** 源创建者fid */
    private Integer originCreateFid;
    /** 源作品征集id列表 */
    private List<Integer> originWorkIds;
    /** 源作品征集名称列表 */
    private List<String> originWorkNames;
    /** 活动标识 */
    private String flag;
    /** 活动市场id */
    private Integer marketId;
    /** 模版id */
    private Integer templateId;

    /**确定活动标识
     * @Description 
     * @author wwb
     * @Date 2021-09-15 15:17:01
     * @param flag
     * @return void
    */
    public void determineFlag(String flag) {
        Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
        Optional.ofNullable(activityFlagEnum).orElseThrow(() -> new BusinessException("未知的活动标识"));
        this.flag = activityFlagEnum.getValue();
    }

    /**构建活动创建对象
     * @Description 
     * @author wwb
     * @Date 2021-09-15 15:05:04
     * @param 
     * @return java.util.List<com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO>
    */
    public List<ActivityCreateParamDTO> buildActivityCreateParamDtos(Integer marketId, Integer templateId) {
        if (StringUtils.isBlank(getOriginCoverCloudId())) {
            this.originCoverCloudId = COVER_CLOUD_ID;
        }
        List<ActivityCreateParamDTO> activityCreateParamDtos = Lists.newArrayList();
        ActivityCreateParamDTO activityCreateParamDto = buildActivityCreateParamDto(marketId, templateId);
        List<Integer> workIds = getOriginWorkIds();
        List<String> workNames = getOriginWorkNames();
        int size = workIds.size();
        if (CollectionUtils.isNotEmpty(workIds)) {
            int index = 0;
            for (Integer originWorkId : workIds) {
                ActivityCreateParamDTO cloneActivityCreateParamDto = new ActivityCreateParamDTO();
                BeanUtils.copyProperties(activityCreateParamDto, cloneActivityCreateParamDto);
                cloneActivityCreateParamDto.setOpenWork(true);
                cloneActivityCreateParamDto.setWorkId(originWorkId);
                cloneActivityCreateParamDto.setName(size > 1 ? workNames.get(index) : cloneActivityCreateParamDto.getName());
                activityCreateParamDtos.add(cloneActivityCreateParamDto);
                index++;
            }
        } else {
            activityCreateParamDtos.add(activityCreateParamDto);
        }
        return activityCreateParamDtos;
    }

    private ActivityCreateParamDTO buildActivityCreateParamDto(Integer marketId, Integer templateId) {
        ActivityCreateParamDTO activityCreateParamDto = ActivityCreateParamDTO.builder()
                .name(getOriginName())
                .coverCloudId(getOriginCoverCloudId())
                .startTimeStamp(getOriginStartTime())
                .endTimeStamp(getOriginEndTime())
                .introduction(getOriginIntroduction())
                .marketId(marketId)
                .templateId(templateId)
                .webTemplateId(WEB_TEMPLATE_ID)
                .activityFlag(getFlag())
                .originType(Activity.OriginTypeEnum.ACTIVITY_RELEASE.getValue())
                .origin(String.valueOf(getOriginId()))
                .build();
        activityCreateParamDto.defaultValue();
        return activityCreateParamDto;
    }

}