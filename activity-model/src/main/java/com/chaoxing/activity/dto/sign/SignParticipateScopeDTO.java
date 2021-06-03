package com.chaoxing.activity.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/28 8:50 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignParticipateScopeDTO {

    private Integer signId;

    private String externalName;
}
