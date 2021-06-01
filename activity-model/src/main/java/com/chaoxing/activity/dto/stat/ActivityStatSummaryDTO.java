package com.chaoxing.activity.dto.stat;

import com.chaoxing.activity.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 2:11 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatSummaryDTO {

    /** 活动id; */
    private Integer activityId;
    /** 报名签到id; */
    private Integer signId;
    /** 活动名称; */
    private String activityName;
    /** 活动门户地址 */
    private String activityPreviewUrl;
    /** 活动状态; */
    private Integer activityStatus;
    /** 活动分类; */
    private String activityClassify;
    /** 活动分类; */
    private Integer activityClassifyId;
    /** 创建者; */
    private String activityCreator;
    /** 创建者; */
    private Integer activityCreateUid;
    /** 参与范围; */
    private String participateScope;
    /** 积分; */
    private BigDecimal integral;
    /** 开始时间; */
    private LocalDateTime startTime;
    /** 结束时间; */
    private LocalDateTime endTime;
    /** 评价数; */
    private Integer rateNum;
    /** 签到数量; */
    private Integer signedInNum;
    /** 签到率; */
    private BigDecimal signInRate;
    /** 合格数量; */
    private Integer qualifiedNum;
    /** 报名人数; */
    private Integer signedUpNum;
    /** 平均参与时长(分钟); */
    private Integer avgParticipateTimeLength;
    /** 创建时间; */
    private LocalDateTime createTime;
    /** 更新时间; */
    private LocalDateTime updateTime;

    /** 起止时间 */
    public String getActivityStartEndTime() {
        if (this.startTime != null && this.endTime != null) {
            String startTimeStr = startTime.format(DateUtils.DATE_MINUTE_TIME_FORMATTER);
            String endTimeStr = endTime.format(DateUtils.DATE_MINUTE_TIME_FORMATTER);
            return startTimeStr + " ~ " + endTimeStr;
        }
        return "";
    }
}
