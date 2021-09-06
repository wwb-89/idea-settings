package com.chaoxing.activity.dto.manager.uc;

import jdk.nashorn.internal.IntDeque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**班级
 * @author wwb
 * @version ver 1.0
 * @className ClazzDTO
 * @description
 * @blame wwb
 * @date 2021-09-03 11:16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClazzDTO {

    /** 班级id */
    private Integer id;
    /** 班级名称 */
    private String name;

}