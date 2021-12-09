package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.enums.OrderTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

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
    private Long startTimestamp;
    /** 活动结束时间yyyy-MM-dd HH:mm:ss */
    private String endTime;
    /** 活动结束时间戳 */
    private Long endTimestamp;
    /** 是否归档 */
    private Boolean archived;
    /** 活动状态 */
    private Integer status;
    /** 活动标识 */
    private String flag;
    /** 机构fid */
    private Integer fid;
    /** start_time, end_time */
    private String orderField;
    /** 排序方式 */
    private String orderType;

    // 非参数传递
    /** 市场id */
    private Integer marketId;

    public void init() {
        List<String> defaultOrderFields = Lists.newArrayList("start_time", "end_time");
        if (getFid() == null) {
            throw new BusinessException("fid不能为空");
        }
        setArchived(Optional.ofNullable(getArchived()).orElse(false));
        // 防止sql注入，先对传入的字段和排序规则做检查
        if (StringUtils.isNotBlank(getOrderType()) && OrderTypeEnum.fromValue(getOrderType()) == null) {
            setOrderType(null);
        }
        if (StringUtils.isNotBlank(getOrderField()) && !defaultOrderFields.contains(getOrderField())) {
            setOrderField(null);
        }
        if (StringUtils.isBlank(getStartTime()) && getStartTimestamp() != null) {
            setStartTime(DateUtils.timestamp2Format(getStartTimestamp(), DateUtils.FULL_DATE_MINUTE_STRING));
        }
        if (StringUtils.isBlank(getEndTime()) && getEndTimestamp() != null) {
            setEndTime(DateUtils.timestamp2Format(getEndTimestamp(), DateUtils.FULL_DATE_MINUTE_STRING));
        }
    }
}
