package com.chaoxing.activity.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**用户活动成绩
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/24 3:31 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResultDTO {

    /** 用户id */
    private Integer uid;

    /** 用户真实姓名 */
    private String realName;

    /** 活动id */
    private Integer activityId;

    /** 签到数 */
    private Integer signedInNum;

    /** 签到率 */
    private BigDecimal signedInRate;

    /** 评论数 */
    private Integer ratingNum;

    /** 总分(积分) */
    private Integer totalScore;

    /** 奖项 */
    private String prize;

    /** 状态: 0：不合格，1：合格，2：待处理 */
    private Integer qualifiedStatus;
}
