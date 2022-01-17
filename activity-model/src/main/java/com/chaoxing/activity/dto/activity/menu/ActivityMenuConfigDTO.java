package com.chaoxing.activity.dto.activity.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/14 11:22 上午
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMenuConfigDTO {

    private Integer id;

    private String name;

    private String code;

    private Boolean enable;

    private Boolean system;

    private Integer sequence;

}
