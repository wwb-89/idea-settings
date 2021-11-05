package com.chaoxing.activity.dto;

import com.chaoxing.activity.util.enums.ConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/11/2 17:12
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionDTO {

    private String name;

    private String value;

    private static ConditionDTO buildFromConditionEnum(ConditionEnum conditionEnum) {
        return ConditionDTO.builder().name(conditionEnum.getName()).value(conditionEnum.getValue()).build();
    }

    /** 列出所有报名条件枚举 */
    public static List<ConditionDTO> list() {
        return Arrays.stream(ConditionEnum.values()).map(ConditionDTO::buildFromConditionEnum).collect(Collectors.toList());
    }

    /** 剔除不限条件，列出其余报名条件 */
    public static List<ConditionDTO> listWithoutNoLimit() {
        return Arrays.stream(ConditionEnum.values()).filter(v -> !Objects.equals(v, ConditionEnum.NO_LIMIT)).map(ConditionDTO::buildFromConditionEnum).collect(Collectors.toList());
    }

}
