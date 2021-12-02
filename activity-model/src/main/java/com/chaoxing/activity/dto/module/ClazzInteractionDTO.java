package com.chaoxing.activity.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**班级互动
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/2 5:36 下午
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClazzInteractionDTO {

    /** 班级id */
    private Integer clazzId;
    /** 课程id */
    private Integer courseId;
}
