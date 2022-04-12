package com.chaoxing.activity.dto.activity.menu;

import com.chaoxing.activity.model.ActivityMenuConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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

    /** 菜单编码 */
    private String menu;

    public static ActivityMenuConfigDTO buildFromActivityMenuConfig(ActivityMenuConfig activityMenuConfig) {
        return ActivityMenuConfigDTO.builder()
                .menu(activityMenuConfig.getMenu())
                .build();
    }

    public static List<ActivityMenuConfigDTO> buildFromActivityMenuConfig(List<ActivityMenuConfig> activityMenuConfigs) {
        return activityMenuConfigs.stream().map(ActivityMenuConfigDTO::buildFromActivityMenuConfig).collect(Collectors.toList());
    }

}
