package com.chaoxing.activity.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/2 16:22
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingModuleDataDTO {

    private String erweima;
    private Integer fid;
    private String icons;
    private Integer id;
    private String instructions;
    private Integer isPublic;
    private Integer isVisible;
    private Integer modulesId;
    private String name;
    private Integer sequence;
    private Integer status;
    private Integer totalNumber;
    private Integer type;
    private Integer uid;
}
