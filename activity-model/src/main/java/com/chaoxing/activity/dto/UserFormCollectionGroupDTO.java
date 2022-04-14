package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/10/18 16:21
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFormCollectionGroupDTO {

    /** 用户id */
    private Integer uid;

    /** 已填写表单数 */
    private Integer filledFormNum;
}
