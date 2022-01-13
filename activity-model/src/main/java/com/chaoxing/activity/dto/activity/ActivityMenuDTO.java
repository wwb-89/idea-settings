package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.dto.module.ClazzInteractionDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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

    /** 菜单名称 */
    private String name;
    /** 菜单编码 */
    private String code;
    /** 是否系统菜单 */
    private Boolean system;
    /** 菜单排序 */
    private Integer sequence;
    /** 自定义应用字段 */
    /** 自定义菜单模板组件id */
    private Integer templateComponentId;
    /** 是否新窗口打开 */
    private Boolean openBlank;
    /** 默认图标url */
    private String defaultIconUrl;
    /** 激活态图标url */
    private String activeIconUrl;
    /** 接口地址 */
    private String url;
    /** 系统菜单才有的描述值，在管理员权限配置处展示 */
    private String desc;

    /**列出所有系统菜单模块
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 15:04:52
     * @return
     */
    public static List<ActivityMenuDTO> listSystemModule() {
        ActivityMenuEnum[] values = ActivityMenuEnum.values();
        return Arrays.stream(values).map(v -> ActivityMenuDTO.builder().name(v.getName()).code(v.getValue()).desc(v.getDesc()).sequence(v.getSequence()).system(true).build()).collect(Collectors.toList());
    }

    /**自定义应用配置转换成菜单name&value
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 14:27:44
     * @param customAppConfigs
     * @return
     */
    public static List<ActivityMenuDTO> convertCustomApps2MenuDTO(List<CustomAppConfig> customAppConfigs) {
        List<ActivityMenuDTO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(customAppConfigs)) {
            return result;
        }
        // 自定义应用菜单排序从100开始
        AtomicInteger sequence = new AtomicInteger(150);
        return customAppConfigs.stream().map(v -> ActivityMenuDTO.builder()
                .name(v.getTitle())
                .code(String.valueOf(v.getId()))
                .templateComponentId(v.getTemplateComponentId())
                .sequence(sequence.incrementAndGet())
                .system(false)
                .openBlank(Optional.ofNullable(v.getOpenBlank()).orElse(false))
                .url(v.getUrl())
                .defaultIconUrl(Optional.ofNullable(v.getDefaultIconCloudId()).map(icon -> DomainConstant.CLOUD_RESOURCE + "/star3/origin/" + icon).orElse(null))
                .activeIconUrl(Optional.ofNullable(v.getActiveIconCloudId()).map(icon -> DomainConstant.CLOUD_RESOURCE + "/star3/origin/" + icon).orElse(null))
                .build()).collect(Collectors.toList());
    }
}
