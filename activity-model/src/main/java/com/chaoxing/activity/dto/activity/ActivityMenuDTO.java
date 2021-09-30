package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityMenuDTO
 * @description
 * @blame wwb
 * @date 2021-08-06 16:07:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMenuDTO {

    private String name;
    private String value;

    public static ActivityMenuDTO buildFromActivityMenuEnum(ActivityMenuEnum activityMenuEnum) {
        return ActivityMenuDTO.builder()
                .name(activityMenuEnum.getName())
                .value(activityMenuEnum.getValue())
                .build();
    }

    /**获取全部菜单values集合
    * @Description
    * @author huxiaolong
    * @Date 2021-09-30 11:45:13
    * @param
    * @return java.util.List<java.lang.String>
    */
    public static List<String> listMenus() {
        return list().stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList());
    }

    public static List<ActivityMenuDTO> list() {
        ActivityMenuEnum[] values = ActivityMenuEnum.values();
        return Arrays.stream(values).map(ActivityMenuDTO::buildFromActivityMenuEnum).collect(Collectors.toList());
    }

    public static List<ActivityMenuDTO> buildFromActivityMenus(List<String> activityMenus) {
        return list().stream().filter(v -> activityMenus.contains(v.getValue())).collect(Collectors.toList());
    }
}
