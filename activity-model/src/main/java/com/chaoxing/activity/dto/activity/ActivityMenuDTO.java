package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.dto.module.ClazzInteractionDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityCustomAppConfig;
import com.chaoxing.activity.model.ActivityMenuConfig;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
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
public class ActivityMenuDTO implements Comparable<ActivityMenuDTO> {

    /** 系统菜单、模板自定义菜单、活动自定义菜单通用字段 */
    /** 菜单名称 */
    private String name;
    /** 菜单编码 */
    private String code;
    /** 菜单类型：frontend 前台, backend 后台 */
    private String type;
    /** 数据来源: 系统枚举， 模板， 活动 */
    private String dataOrigin;
    /** 是否新窗口打开 */
    private Boolean openBlank;
    /** 接口地址 */
    private String url;
    /** 是否启用 */
    private Boolean enable;
    /** 系统菜单才有的描述值，在管理员权限配置处展示 */
    private String desc;
    /** 默认图标url */
    private String defaultIconUrl;
    /** 激活态图标url */
    private String activeIconUrl;
    /** 排序字段 */
    private Integer sequence;

    /** 模板自定义配置字段 */
    /** 自定义菜单模板组件id */
    private Integer templateComponentId;

    /** 活动自定义菜单字段 */
    /** 显示规则 */
    private String showRule;
    /** 是否pc端菜单 */
    private Boolean pc;
    /** 是否移动端菜单 */
    private Boolean mobile;


    /**列出所有系统菜单模块（含前后端）
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 15:04:52
     * @return
     */
    public static List<ActivityMenuDTO> listSystemModule() {
        ActivityMenuEnum.BackendMenuEnum[] backendMenuEnums = ActivityMenuEnum.BackendMenuEnum.values();
        ActivityMenuEnum.FrontendMenuEnum[] frontendMenuEnums = ActivityMenuEnum.FrontendMenuEnum.values();
        List<ActivityMenuDTO> systemMenus = Lists.newArrayList();
        systemMenus.addAll(Arrays.stream(backendMenuEnums).map(v -> ActivityMenuDTO.builder()
                .name(v.getName())
                .code(v.getValue())
                .type(ActivityMenuConfig.UrlTypeEnum.BACKEND.getValue())
                .dataOrigin(ActivityMenuConfig.DataOriginEnum.SYSTEM.getValue())
                .openBlank(false)
                .url("")
                .desc(v.getDesc())
                .showRule(ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue())
                .sequence(v.getSequence())
                .build()).collect(Collectors.toList()));
        systemMenus.addAll(Arrays.stream(frontendMenuEnums).map(v -> ActivityMenuDTO.builder()
                .name(v.getName())
                .code(v.getValue())
                .type(ActivityMenuConfig.UrlTypeEnum.FRONTEND.getValue())
                .dataOrigin(ActivityMenuConfig.DataOriginEnum.SYSTEM.getValue())
                .openBlank(true)
                .url("")
                .desc("")
                .showRule(ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue())
                .sequence(v.getSequence())
                .build()).collect(Collectors.toList()));
        return systemMenus;
    }

    /**自定义应用配置转换成菜单name&value
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 14:27:44
     * @param customAppConfigs
     * @return
     */
    public static List<ActivityMenuDTO> convertTplCustomApps2MenuDTO(List<CustomAppConfig> customAppConfigs) {
        List<ActivityMenuDTO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(customAppConfigs)) {
            return result;
        }
        return customAppConfigs.stream().map(v -> ActivityMenuDTO.builder()
                .name(v.getTitle())
                .code(String.valueOf(v.getId()))
                .type(v.getType())
                .dataOrigin(ActivityMenuConfig.DataOriginEnum.TEMPLATE.getValue())
                .openBlank(Optional.ofNullable(v.getOpenBlank()).orElse(false))
                .url(v.getUrl())
                .desc("")
                .showRule(ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue())
                .defaultIconUrl(Optional.ofNullable(v.getDefaultIconCloudId()).map(icon -> DomainConstant.CLOUD_RESOURCE + "/star3/origin/" + icon).orElse(null))
                .activeIconUrl(Optional.ofNullable(v.getActiveIconCloudId()).map(icon -> DomainConstant.CLOUD_RESOURCE + "/star3/origin/" + icon).orElse(null))
                .templateComponentId(v.getTemplateComponentId())
                .build()).collect(Collectors.toList());
    }

    public static List<ActivityMenuDTO> convertActivityCustomApps2MenuDTO(List<ActivityCustomAppConfig> activityCustomApps) {
        List<ActivityMenuDTO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(activityCustomApps)) {
            return result;
        }
        return activityCustomApps.stream().map(v -> ActivityMenuDTO.builder()
                .name(v.getTitle())
                .code(String.valueOf(v.getId()))
                .type(v.getType())
                .dataOrigin(ActivityMenuConfig.DataOriginEnum.ACTIVITY.getValue())
                .openBlank(false)
                .url(v.getUrl())
                .desc("")
                .defaultIconUrl(Optional.ofNullable(v.getDefaultIconCloudId()).map(icon -> DomainConstant.CLOUD_RESOURCE + "/star3/origin/" + icon).orElse(null))
                .activeIconUrl(Optional.ofNullable(v.getActiveIconCloudId()).map(icon -> DomainConstant.CLOUD_RESOURCE + "/star3/origin/" + icon).orElse(null))
                .showRule(Optional.ofNullable(v.getShowRule()).orElse(ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue()))
                .pc(v.getPc())
                .mobile(v.getMobile())
                .build()).collect(Collectors.toList());
    }

    @Override
    public int compareTo(ActivityMenuDTO obj) {
        int o1SeqWeight = Optional.ofNullable(getSequence()).orElse(1000);
        int o2SeqWeight = Optional.ofNullable(obj.getSequence()).orElse(1000);
        if (o1SeqWeight != o2SeqWeight) {
            return o1SeqWeight > o2SeqWeight ? 1 : -1;
        }
        Integer o1DataOriginWeight = Optional.ofNullable(ActivityMenuConfig.DataOriginEnum.fromValue(getDataOrigin())).map(ActivityMenuConfig.DataOriginEnum::getWeight).orElse(ActivityMenuConfig.DataOriginEnum.ACTIVITY.getWeight());
        Integer o2DataOriginWeight = Optional.ofNullable(ActivityMenuConfig.DataOriginEnum.fromValue(obj.getDataOrigin())).map(ActivityMenuConfig.DataOriginEnum::getWeight).orElse(ActivityMenuConfig.DataOriginEnum.ACTIVITY.getWeight());
        return o1DataOriginWeight - o2DataOriginWeight;
    }


}
