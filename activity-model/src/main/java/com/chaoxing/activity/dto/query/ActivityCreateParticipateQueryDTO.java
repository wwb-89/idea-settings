package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.enums.OrderTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 活动管理第三方查询参数DTO
 * @author: huxiaolong
 * @date: 2021/11/30 6:52 下午
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateParticipateQueryDTO {

    /** 搜索内容 */
    private String sw;
    /** 活动开始时间yyyy-MM-dd HH:mm:ss */
    private String startTime;
    /** 活动开始时间戳 */
    private Long startTimeStamp;
    /** 活动结束时间yyyy-MM-dd HH:mm:ss */
    private String endTime;
    /** 活动结束时间戳 */
    private Long endTimeStamp;
    /** 是否归档 */
    private Boolean archived;
    /** 活动状态 */
    private Integer status;
    /** 机构fid */
    private Integer fid;
    /** start_time, end_time */
    private String orderField;
    /** 排序方式 */
    private OrderTypeEnum orderType;

    public void init() {
        if (getFid() == null) {
            throw new BusinessException("参数fid缺失");
        }
        if (StringUtils.isBlank(getStartTime()) && getStartTimeStamp() != null) {
            setStartTime(DateUtils.timestamp2Format(getStartTimeStamp(), DateUtils.FULL_DATE_MINUTE_STRING));
        }
        if (StringUtils.isBlank(getEndTime()) && getEndTimeStamp() != null) {
            setEndTime(DateUtils.timestamp2Format(getEndTimeStamp(), DateUtils.FULL_DATE_MINUTE_STRING));
        }
    }
}
