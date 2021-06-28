package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/25 7:50 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResultQueryDTO {

    /** 活动ID */
    private Integer activityId;
    /** 关键字 */
    private String sw;
    /** 合格状态 */
    private Integer qualifiedStatus;
    /** 排序字段id */
    private Integer orderFieldId;
    /** 排序字段 */
    private String orderField;
    /** 排序方式 */
    private OrderTypeEnum orderType;
}
