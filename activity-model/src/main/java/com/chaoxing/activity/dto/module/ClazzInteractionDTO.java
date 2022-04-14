package com.chaoxing.activity.dto.module;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

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
