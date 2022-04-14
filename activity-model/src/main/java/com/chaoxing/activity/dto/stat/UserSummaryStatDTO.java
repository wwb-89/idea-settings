package com.chaoxing.activity.dto.stat;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/10/22 14:30
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryStatDTO {

    /** 用户id */
    private Integer uid;

    /** 用户真实姓名 */
    private String realName;

    /** 用户活动积分总和 */
    private BigDecimal integralSum;
}
