package com.chaoxing.activity.dto.tablefield;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 11:29 上午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableFieldDTO {
    /** 主键; */
    private Integer id;
    /** 表格字段配置id; */
    private Integer tableFieldId;
    /** 名称; */
    private String name;
    /** 编码; */
    private String code;
    /** 是否选中; */
    private Boolean defaultChecked;
    /** 是否置顶; */
    private Boolean defaultTop;
    /** 顺序; */
    private Integer sequence;
    /** 创建时间; */
    private LocalDateTime createTime;
    /** 更新时间; */
    private LocalDateTime updateTime;


}
