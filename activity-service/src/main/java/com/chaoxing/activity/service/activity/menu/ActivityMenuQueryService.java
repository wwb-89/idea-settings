package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.mapper.ActivityMenuConfigMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.engine.CustomAppConfigQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerValidationService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**活动菜单查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityMenuService
 * @description
 * @blame wwb
 * @date 2021-08-06 16:10:13
 */
@Slf4j
@Service
public class ActivityMenuQueryService {

    @Resource
    private ActivityManagerValidationService activityManagerValidationService;
    @Resource
    private ActivityManagerService activityManagerService;
    @Resource
    private ActivityMenuConfigMapper activityMenuConfigMapper;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private CustomAppConfigQueryService customAppConfigQueryService;
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private ActivityCustomAppConfigQueryService activityCustomAppConfigQueryService;

    /**创建时，查询全部菜单配置
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 18:49:56
     * @param activityId
     * @param templateId
     * @return
     */
    public List<ActivityMenuConfig> listActivityAllDefaultMenus(Integer activityId, Integer templateId) {
        // 系统的默认全部菜单模块（前后端）
        List<ActivityMenuDTO> activityMenus = Lists.newArrayList(ActivityMenuDTO.listSystemModule());
        // 获取活动对应的模板，查看是否存在自定义模板应用，若有，则全部加入管理配置
        if (templateId != null) {
            List<CustomAppConfig> customAppConfigs = customAppConfigQueryService.listByTemplateId(templateId);
            if (CollectionUtils.isNotEmpty(customAppConfigs)) {
                activityMenus.addAll(ActivityMenuDTO.convertTplCustomApps2MenuDTO(customAppConfigs));
            }
        }
        return resortMenus(activityMenus).stream().map(v -> ActivityMenuConfig.builder()
                .activityId(activityId)
                .menu(v.getCode())
                .dataOrigin(v.getDataOrigin())
                .showRule(v.getShowRule())
                .enable(Boolean.TRUE)
                .templateComponentId(v.getTemplateComponentId())
                .sequence(v.getSequence())
                .type(v.getType())
                .build()).collect(Collectors.toList());
    }

    private List<ActivityMenuDTO> resortMenus(List<ActivityMenuDTO> waitSortedMenus) {
        List<ActivityMenuDTO> frontendMenus = waitSortedMenus.stream().filter(v -> Objects.equals(v.getType(), ActivityMenuConfig.UrlTypeEnum.FRONTEND.getValue())).sorted().collect(Collectors.toList());
        List<ActivityMenuDTO> backendMenus = waitSortedMenus.stream().filter(v -> Objects.equals(v.getType(), ActivityMenuConfig.UrlTypeEnum.BACKEND.getValue())).sorted().collect(Collectors.toList());
        AtomicInteger frontendSeq = new AtomicInteger(0);
        AtomicInteger backendSeq = new AtomicInteger(0);
        backendMenus.forEach(v -> v.setSequence(backendSeq.incrementAndGet() * 10));
        frontendMenus.forEach(v -> v.setSequence(frontendSeq.incrementAndGet() * 10));
        List<ActivityMenuDTO> activityMenus = Lists.newArrayList(backendMenus);
        activityMenus.addAll(frontendMenus);
        return activityMenus;
    }

    /**获取活动已配置的菜单列表
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 19:01:12
     * @param activityId
     * @return
     */
    public List<ActivityMenuConfig> listByActivityId(Integer activityId) {
        return activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
                .orderByAsc(ActivityMenuConfig::getSequence)
        );
    }

    /**获取活动已配置且启用的的菜单列表
     * @Description 
     * @author wwb
     * @Date 2022-04-12 15:11:47
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.ActivityMenuConfig>
    */
    public List<ActivityMenuConfig> listEnableByActivityId(Integer activityId) {
        return activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
                .eq(ActivityMenuConfig::getEnable, true)
                .orderByAsc(ActivityMenuConfig::getSequence)
        );
    }

    /**获取活动可配置的菜单列表
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 19:05:23
     * @param activityId
     * @return
     */
    public List<ActivityMenuDTO> listCanConfigMenus(Integer activityId) {
        // 系统固有的菜单
        List<ActivityMenuDTO> canConfigMenus = Lists.newArrayList(ActivityMenuDTO.listSystemModule());
        // 查询活动已配置自定义菜单中的templateComponentId
        List<ActivityMenuConfig> menuConfigs = listByActivityId(activityId);
        List<Integer> existMenuTplComponentIds = menuConfigs.stream().map(ActivityMenuConfig::getTemplateComponentId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<CustomAppConfig> customAppConfigs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existMenuTplComponentIds)) {
            // 查询所有customAppConfigs含删除
            List<CustomAppConfig> appConfigs = customAppConfigQueryService.listWithDeleted(existMenuTplComponentIds);
            customAppConfigs.addAll(appConfigs);
        }
        Activity activity = activityQueryService.getById(activityId);
        boolean openClazzInteraction = Optional.ofNullable(activity.getOpenClazzInteraction()).orElse(false);
        // 过滤掉班级互动
        if (!openClazzInteraction) {
            canConfigMenus = canConfigMenus.stream().filter(v -> !ActivityMenuEnum.isClazzInteractionMenu(v.getCode())).collect(Collectors.toList());
        }
        // 获取模板的自定义应用组件templateComponentId
        List<Integer> customAppTplComponentIds = templateComponentService.listCustomAppComponentTplComponentIds(activity.getTemplateId());
        if (CollectionUtils.isNotEmpty(customAppTplComponentIds)) {
            customAppTplComponentIds.removeAll(existMenuTplComponentIds);
            List<CustomAppConfig> appConfigs = customAppConfigQueryService.list(customAppTplComponentIds);
            customAppConfigs.addAll(appConfigs);
        }
        if (CollectionUtils.isNotEmpty(customAppConfigs)) {
            canConfigMenus.addAll(ActivityMenuDTO.convertTplCustomApps2MenuDTO(customAppConfigs));
        }
        List<ActivityCustomAppConfig> activityCustomApps = activityCustomAppConfigQueryService.listActivityCustomApp(activityId);
        if (CollectionUtils.isNotEmpty(activityCustomApps)) {
            canConfigMenus.addAll(ActivityMenuDTO.convertActivityCustomApps2MenuDTO(activityCustomApps));
        }
        Map<String, ActivityMenuConfig> menuConfigMap = menuConfigs.stream().collect(Collectors.toMap(ActivityMenuConfig::getMenu, v -> v, (v1, v2) -> v2));
        canConfigMenus.forEach(v -> {
            ActivityMenuConfig menuConfig = menuConfigMap.get(v.getCode());
            Integer seq = Optional.ofNullable(menuConfig).map(ActivityMenuConfig::getSequence).orElse(1000);
            Boolean enable = Optional.ofNullable(menuConfig).map(ActivityMenuConfig::getEnable).orElse(false);
            if (menuConfig != null) {
                String showRule = StringUtils.isBlank(menuConfig.getShowRule()) ? ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue() : menuConfig.getShowRule();
                v.setShowRule(showRule);
            }
            v.setId(Optional.ofNullable(menuConfig).map(ActivityMenuConfig::getId).orElse(null));
            v.setActivityId(activityId);
            v.setSequence(seq);
            v.setEnable(enable);
        });
        return resortMenus(canConfigMenus);
    }

    /**查询活动启用的菜单列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 18:56:03
     * @param activityId
     * @return 
     */
    public List<ActivityMenuConfig> listActivityEnableMenuConfigs(Integer activityId) {
        return activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
                .eq(ActivityMenuConfig::getEnable, true)
                .orderByAsc(ActivityMenuConfig::getSequence));
    }

    /**
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-20 11:32:05
     * @param activityId
     * @return 
     */
    public List<ActivityMenuDTO> listActivityEnableBackendMenus(Integer activityId) {
        List<ActivityMenuDTO> result = convertMenu2DTO(activityId, listActivityEnableMenuConfigs(activityId));
        return result.stream().filter(v -> Objects.equals(ActivityMenuConfig.UrlTypeEnum.BACKEND.getValue(), v.getType())).collect(Collectors.toList());
    }

    /**当前用户活动管理主页菜单列表查询，仅查询启用的
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 14:46:05
     * @param activity
     * @param uid
     * @param creator
     * @return
     */
    public List<ActivityMenuDTO> listUserActivityMenus(Activity activity, Integer uid, boolean creator, Boolean isMobile) {
        Integer activityId = activity.getId();
        List<ActivityMenuConfig> activityMenus = listCreatorMenus(activityId);
        if (creator) {
            return convertMenuConfig2DTO(activityId, activityMenus, isMobile);
        }
        if (!activityManagerValidationService.isManager(activityId, uid)) {
            return Lists.newArrayList();
        }
        ActivityManager activityManager = activityManagerService.getByActivityUid(activityId, uid);
        if (activityManager == null || StringUtils.isBlank(activityManager.getMenu())) {
            return Lists.newArrayList();
        }
        // 如果用户为管理员，且有menu配置，根据menu配置进一步过滤活动菜单配置列表
        List<String> managerMenus = Arrays.asList(StringUtils.split(activityManager.getMenu(), ","));
        activityMenus = activityMenus.stream().filter(v -> managerMenus.contains(v.getMenu())).collect(Collectors.toList());
        return convertMenuConfig2DTO(activityId, activityMenus, isMobile);
    }

    /**转换菜单
     * @Description
     * @author huxiaolong
     * @Date 2022-03-03 11:15:58
     * @param activityId
     * @param activityMenus
     * @return
     */
    private List<ActivityMenuDTO> convertMenuConfig2DTO(Integer activityId, List<ActivityMenuConfig> activityMenus, Boolean isMobile) {
        isMobile = Optional.ofNullable(isMobile).orElse(false);
        List<ActivityMenuDTO> result = convertMenu2DTO(activityId, activityMenus).stream()
                .filter(v ->
                        Objects.equals(ActivityMenuConfig.UrlTypeEnum.BACKEND.getValue(), v.getType())
                ).collect(Collectors.toList());
        if (isMobile) {
            return result.stream().filter(ActivityMenuDTO::getMobile).collect(Collectors.toList());
        }
        return result.stream().filter(ActivityMenuDTO::getPc).collect(Collectors.toList());
    }

    /**列出活动创建者所有菜单配置
     * @Description
     * @author huxiaolong
     * @Date 2022-03-03 11:16:32
     * @param activityId
     * @return
     */
    private List<ActivityMenuConfig> listCreatorMenus(Integer activityId) {
        // 查询活动配置启用的所有菜单
        List<ActivityMenuConfig> activityMenus = listActivityEnableMenuConfigs(activityId);
        // 旧数据中可能没有设置配置，所以在此添加上设置，并置为启用状态，若用户不为创建者，在下面的管理员的判断中会自动过滤掉该多余配置
        ActivityMenuEnum.BackendMenuEnum settingEnum = ActivityMenuEnum.BackendMenuEnum.SETTING;
        ActivityMenuConfig settingMenu = activityMenus.stream().filter(v -> Objects.equals(v.getMenu(), ActivityMenuEnum.BackendMenuEnum.SETTING.getValue())).findFirst().orElse(null);
        if (settingMenu == null) {
            activityMenus.add(ActivityMenuConfig.builder()
                    .menu(settingEnum.getValue())
                    .showRule(ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue())
                    .enable(true)
                    .dataOrigin(ActivityMenuConfig.DataOriginEnum.SYSTEM.getValue())
                    .type(ActivityMenuConfig.UrlTypeEnum.BACKEND.getValue())
                    .sequence(settingEnum.getSequence())
                    .build());
        }
        return activityMenus;
    }

    /**MenuConfig转换MenuDTO，携带上url和图标等信息
     * @Description
     * @author huxiaolong
     * @Date 2022-01-11 16:26:50
     * @param activityMenuConfigs
     * @return
     */
    private List<ActivityMenuDTO> convertMenu2DTO(Integer activityId, List<ActivityMenuConfig> activityMenuConfigs) {
        if (CollectionUtils.isEmpty(activityMenuConfigs)) {
            return Lists.newArrayList();
        }
        // 系统模块
        Map<String, ActivityMenuDTO> systemModuleMap = ActivityMenuDTO.listSystemModule().stream().collect(Collectors.toMap(ActivityMenuDTO::getCode, v -> v, (v1, v2) -> v2));
        // 自定义组件菜单列表
        List<Integer> tplComponentIds = activityMenuConfigs.stream().map(ActivityMenuConfig::getTemplateComponentId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<ActivityMenuDTO> customAppMenus = ActivityMenuDTO.convertTplCustomApps2MenuDTO(customAppConfigQueryService.listWithDeleted(tplComponentIds));
        Map<String, ActivityMenuDTO> customAppMap = customAppMenus.stream().collect(Collectors.toMap(ActivityMenuDTO::getCode, v -> v, (v1, v2) -> v2));
        // 活动自定义菜单列表
        List<ActivityCustomAppConfig> activityCustomMenuConfigs = activityCustomAppConfigQueryService.listActivityCustomApp(activityId);
        List<ActivityMenuDTO> activityCustomAppMenus = ActivityMenuDTO.convertActivityCustomApps2MenuDTO(activityCustomMenuConfigs);
        Map<String, ActivityMenuDTO> activityCustomMenuMap = activityCustomAppMenus.stream().collect(Collectors.toMap(ActivityMenuDTO::getCode, v -> v, (v1, v2) -> v2));
        List<ActivityMenuDTO> result = Lists.newArrayList();
        int sequence = 1000;
        // 遍历菜单， 进行相关地址和图标属性进行填充
        for (ActivityMenuConfig menuConfig : activityMenuConfigs) {
            String menu = menuConfig.getMenu();
            //  seq以活动配置了的为准
            Integer seq = Optional.ofNullable(menuConfig.getSequence()).orElse(sequence++);
            ActivityMenuDTO menuDTO = Optional.ofNullable(systemModuleMap.get(menu))
                    .orElse(Optional.ofNullable(customAppMap.get(menu))
                            .orElse(activityCustomMenuMap.get(menu)));
            if (menuDTO != null) {
                menuDTO.setSequence(seq);
                result.add(menuDTO);
            }
        }
        return result.stream().sorted(Comparator.comparing(ActivityMenuDTO::getSequence)).collect(Collectors.toList());
    }

}