package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityMenuService
 * @description
 * @blame wwb
 * @date 2021-08-06 16:10:13
 */
@Slf4j
@Service
public class ActivityMenuService {
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

    /**创建时，查询全部菜单配置
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 18:49:56
     * @param activityId
     * @param templateId
     * @return
     */
    public List<ActivityMenuConfig> listActivityAllDefaultMenus(Integer activityId, Integer templateId) {
        // 系统的默认全部菜单模块 + 班级互动
        List<ActivityMenuDTO> activityMenus = Lists.newArrayList();
        activityMenus.addAll(ActivityMenuDTO.listSystemModule());
        activityMenus.addAll(ActivityMenuDTO.listDefaultClazzInteractionMenus());
        // 获取活动对应的模板，查看是否存在自定义模板应用，若有，则全部加入管理配置
        if (templateId != null) {
            List<CustomAppConfig> customAppConfigs = customAppConfigQueryService.listBackend(templateId);
            if (CollectionUtils.isNotEmpty(customAppConfigs)) {
                activityMenus.addAll(ActivityMenuDTO.convertCustomApps2MenuDTO(customAppConfigs));
            }
        }
        return activityMenus.stream().map(v -> ActivityMenuConfig.builder()
                .activityId(activityId)
                .menu(v.getCode())
                .system(v.getSystem())
                .enable(Boolean.TRUE)
                .templateComponentId(v.getTemplateComponentId())
                .build()).collect(Collectors.toList());
    }

    /**获取活动已配置的菜单列表
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 19:01:12
     * @param activityId
     * @return
     */
    public List<ActivityMenuConfig> listByActivityId(Integer activityId) {
        return activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>().eq(ActivityMenuConfig::getActivityId, activityId));
    }

    /**获取活动可配置的菜单列表
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 19:05:23
     * @param activityId
     * @return
     */
    public List<ActivityMenuDTO> listCanConfigMenus(Integer activityId) {
        // 查询活动已配置自定义菜单中的templateComponentId
        List<Integer> existMenuTplComponentIds = listByActivityId(activityId)
                .stream()
                .map(ActivityMenuConfig::getTemplateComponentId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<CustomAppConfig> customAppConfigs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existMenuTplComponentIds)) {
            // 查询所有customAppConfigs含删除
            List<CustomAppConfig> appConfigs = customAppConfigQueryService.listBackendWithDeleted(existMenuTplComponentIds);
            customAppConfigs.addAll(appConfigs);
        }
        Activity activity = activityQueryService.getById(activityId);
        // 系统固有的模板组件
        List<ActivityMenuDTO> canConfigMenus = Lists.newArrayList();
        canConfigMenus.addAll(ActivityMenuDTO.listSystemModule());
        canConfigMenus.addAll(ActivityMenuDTO.listDefaultClazzInteractionMenus());
        // 获取模板的自定义应用组件templateComponentId
        List<Integer> customAppTplComponentIds = templateComponentService.listCustomAppComponentTplComponentIds(activity.getId());
        if (CollectionUtils.isNotEmpty(customAppTplComponentIds)) {
            customAppTplComponentIds.removeAll(existMenuTplComponentIds);
            List<CustomAppConfig> appConfigs = customAppConfigQueryService.listBackend(customAppTplComponentIds);
            customAppConfigs.addAll(appConfigs);
        }
        if (CollectionUtils.isNotEmpty(customAppConfigs)) {
            canConfigMenus.addAll(ActivityMenuDTO.convertCustomApps2MenuDTO(customAppConfigs));
        }
        return canConfigMenus;
    }

    /**查询活动启用的菜单列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 18:56:03
     * @param activityId
     * @return 
     */
    public List<ActivityMenuConfig> listActivityEnableMenus(Integer activityId) {
        return listByActivityId(activityId).stream().filter(v -> Objects.equals(v.getEnable(), Boolean.TRUE)).collect(Collectors.toList());
    }

    public List<ActivityMenuDTO> listActivityEnableMenusDTO(Integer activityId) {
        return convertMenu2DTO(new Activity(), null, null, listActivityEnableMenus(activityId));
    }

    /**查询当前用户可见的菜单配置列表(活动主页用户菜单列表查询)
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 14:46:05
     * @param activity
     * @param loginUser
     * @param creator
     * @return
     */
    public List<ActivityMenuDTO> listUserActivityMenus(Activity activity, LoginUserDTO loginUser, boolean creator) {
        Integer activityId = activity.getId();
        Integer uid = loginUser.getUid();
        Integer fid = loginUser.getFid();

        // 查询活动配置的所有菜单
        List<ActivityMenuConfig> activityMenus = listActivityEnableMenus(activityId);
        if (activityManagerValidationService.isManager(activityId, uid) && !creator) {
            ActivityManager activityManager = activityManagerService.getByActivityUid(activityId, uid);
            if (activityManager != null && StringUtils.isNotBlank(activityManager.getMenu())) {
                // 列出管理员配置的菜单
                List<String> managerMenus = Arrays.asList(StringUtils.split(activityManager.getMenu(), ","));
                activityMenus = activityMenus.stream().filter(v -> managerMenus.contains(v.getMenu())).collect(Collectors.toList());
            }
        }
        return convertMenu2DTO(activity, uid, fid, activityMenus);
    }

    /**MenuConfig转换MenuDTO，携带上url和图标等信息
     * @Description
     * @author huxiaolong
     * @Date 2022-01-11 16:26:50
     * @param activity
     * @param uid
     * @param fid
     * @param activityMenuConfigs
     * @return
     */
    private List<ActivityMenuDTO> convertMenu2DTO(Activity activity, Integer uid, Integer fid, List<ActivityMenuConfig> activityMenuConfigs) {
        if (CollectionUtils.isEmpty(activityMenuConfigs)) {
            return Lists.newArrayList();
        }
        // 系统模块
        Map<String, ActivityMenuDTO> systemModuleMap = ActivityMenuDTO.listSystemModule().stream().collect(Collectors.toMap(ActivityMenuDTO::getCode, v -> v, (v1, v2) -> v2));
        // 班级互动
        Map<String, ActivityMenuDTO> classInteractionMap = ActivityMenuDTO.convertClazzInteractions2MenuDTO(activity, uid, fid).stream().collect(Collectors.toMap(ActivityMenuDTO::getCode, v -> v, (v1, v2) -> v2));
        // 自定义组件菜单列表
        List<Integer> tplComponentIds = activityMenuConfigs.stream().map(ActivityMenuConfig::getTemplateComponentId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<ActivityMenuDTO> customAppMenus = ActivityMenuDTO.convertCustomApps2MenuDTO(customAppConfigQueryService.listBackendWithDeleted(tplComponentIds));
        Map<String, ActivityMenuDTO> customAppMap = customAppMenus.stream().collect(Collectors.toMap(ActivityMenuDTO::getCode, v -> v, (v1, v2) -> v2));
        List<ActivityMenuDTO> result = Lists.newArrayList();
        // 遍历菜单， 进行相关地址和图标属性进行填充
        for (ActivityMenuConfig menuConfig : activityMenuConfigs) {
            String menu = menuConfig.getMenu();
            ActivityMenuDTO menuDTO = systemModuleMap.get(menu);
            if (menuDTO != null) {
                result.add(menuDTO);
                continue;
            }
            menuDTO = classInteractionMap.get(menu);
            if (menuDTO != null) {
                result.add(menuDTO);
                continue;
            }
            menuDTO = customAppMap.get(menu);
            if (menuDTO != null) {
                result.add(menuDTO);
            }
        }
        return result.stream().sorted(Comparator.comparing(ActivityMenuDTO::getSequence)).collect(Collectors.toList());
    }

    /**活动创建时，批量新增默认的菜单列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-11 10:34:24
     * @param realActivityId
     * @param activityMenus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void configActivityDefaultMenu(Integer realActivityId, List<ActivityMenuConfig> activityMenus) {
        if (CollectionUtils.isEmpty(activityMenus)) {
            return;
        }
        activityMenus.forEach(v -> v.setActivityId(realActivityId));
        activityMenuConfigMapper.batchAdd(activityMenus);
    }

    /**活动菜单配置
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-11 11:12:20
     * @param activityId
     * @param menuList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void configActivityMenu(Integer activityId, List<String> menuList) {
        // 查询活动现有的活动菜单配置
        Map<String, ActivityMenuConfig> menuConfigMap = listByActivityId(activityId).stream().collect(Collectors.toMap(ActivityMenuConfig::getMenu, v -> v, (v1, v2) -> v2));
        List<ActivityMenuConfig> waitAddConfigs = Lists.newArrayList();
        List<ActivityMenuConfig> waitUpdateConfigs = Lists.newArrayList();
        menuList.forEach(menu -> {
            ActivityMenuConfig menuConfig = menuConfigMap.get(menu);
            if (menuConfig == null) {
                waitAddConfigs.add(ActivityMenuConfig.builder().menu(menu).activityId(activityId).system(false).enable(true).build());
            }
        });
        menuConfigMap.values().forEach(v -> {
            boolean isUpdated = (menuList.contains(v.getMenu()) && !v.getEnable()) || (!menuList.contains(v.getMenu()) && v.getEnable());
            if (isUpdated) {
                v.setEnable(!v.getEnable());
                waitUpdateConfigs.add(v);
            }
        });
        if (CollectionUtils.isNotEmpty(waitAddConfigs)) {
            activityMenuConfigMapper.batchAdd(waitAddConfigs);
        }
        if (CollectionUtils.isNotEmpty(waitUpdateConfigs)) {
            waitUpdateConfigs.forEach(v -> activityMenuConfigMapper.update(null, new LambdaUpdateWrapper<ActivityMenuConfig>()
                    .eq(ActivityMenuConfig::getId, v.getId())
                    .set(ActivityMenuConfig::getEnable, v.getEnable())));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateActivityMenusByInspectionConfig(Integer activityId, boolean openInspectionConfig) {
        String inspectManage = ActivityMenuEnum.RESULTS_MANAGE.getValue();
        ActivityMenuConfig inspectConfigMenu = activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
                .eq(ActivityMenuConfig::getMenu, inspectManage)).stream().findFirst().orElse(null);
        if (inspectConfigMenu == null) {
            activityMenuConfigMapper.insert(ActivityMenuConfig.builder()
                            .activityId(activityId)
                            .menu(inspectManage)
                            .system(true)
                            .enable(openInspectionConfig)
                            .build());
        } else {
            boolean isUpdated = (openInspectionConfig && !inspectConfigMenu.getEnable()) || (!openInspectionConfig && inspectConfigMenu.getEnable());
            if (isUpdated) {
                activityMenuConfigMapper.update(null, new LambdaUpdateWrapper<ActivityMenuConfig>()
                        .eq(ActivityMenuConfig::getId, inspectConfigMenu.getId())
                        .set(ActivityMenuConfig::getEnable, !inspectConfigMenu.getEnable()));
            }
        }
    }


}