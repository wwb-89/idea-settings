package com.chaoxing.activity.dto;

import com.chaoxing.activity.model.UserActionRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户个人行为记录成绩
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
public class UserGradeDTO {

    /** 用户id */
    private Integer uid;
    /** 用户真实姓名 */
    private String realName;
    /** 活动id */
    private Integer activityId;
    /** 活动id */
    private String activityName;
    /** 总分(积分) */
    private Integer totalScore;

     List<UserActionRecord> userActionRecords;
}
