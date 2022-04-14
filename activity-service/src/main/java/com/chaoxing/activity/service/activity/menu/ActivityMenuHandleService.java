package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.mapper.ActivityMenuConfigMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityCustomAppConfig;
import com.chaoxing.activity.model.ActivityMenuConfig;
import com.chaoxing.activity.model.FlagDefaultSystemMenuConfig;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**活动菜单处理服务
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/18 4:22 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class ActivityMenuHandleService {

    @Resource
    private ActivityMenuConfigMapper activityMenuConfigMapper;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityCustomAppConfigHandleService activityCustomAppConfigHandleService;
    @Resource
    private FlagDefaultSystemMenuConfigService flagDefaultSystemMenuConfigService;

    /**活动创建时，批量新增默认的菜单列表
     * @Description 活动标识配置了默认的菜单配置，此处活动的默认菜单需要合并活动标识的默认配置：活动标识列表有些是不启用的
     * @author huxiaolong
     * @Date 2022-01-11 10:34:24
     * @param activityId
     * @param activityMenus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void configActivityDefaultMenu(Integer activityId, List<ActivityMenuConfig> activityMenus) {
        if (CollectionUtils.isEmpty(activityMenus)) {
            return;
        }
        Activity activity = activityQueryService.getById(activityId);
        List<FlagDefaultSystemMenuConfig> flagDefaultSystemMenuConfigs = flagDefaultSystemMenuConfigService.listByFlag(activity.getActivityFlag());
        if (CollectionUtils.isNotEmpty(flagDefaultSystemMenuConfigs)) {
            // 系统菜单以配置为准
            Map<String, FlagDefaultSystemMenuConfig> menuObjectMap = flagDefaultSystemMenuConfigs.stream().collect(Collectors.toMap(FlagDefaultSystemMenuConfig::getMenu, v -> v, (v1, v2) -> v2));
            Iterator<ActivityMenuConfig> iterator = activityMenus.iterator();
            while (iterator.hasNext()) {
                ActivityMenuConfig next = iterator.next();
                String dataOrigin = next.getDataOrigin();
                if (Objects.equals(ActivityMenuConfig.DataOriginEnum.SYSTEM.getValue(), dataOrigin)) {
                    // menu唯一（所有的来源：系统、模板、活动）
                    FlagDefaultSystemMenuConfig flagDefaultSystemMenuConfig = menuObjectMap.get(next.getMenu());
                    if (flagDefaultSystemMenuConfig == null) {
                        iterator.remove();
                    } else {
                        next.setSequence(flagDefaultSystemMenuConfig.getSequence());
                        next.setShowRule(flagDefaultSystemMenuConfig.getShowRule());
                    }
                }
            }
        }
        activityMenus.forEach(v -> v.setActivityId(activityId));
        activityMenuConfigMapper.batchAdd(activityMenus);
    }


    /**考核配置更新时，菜单同步更新
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-18 16:26:20
     * @param activityId
     * @param openInspectionConfig
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityMenusByInspectionConfig(Integer activityId, boolean openInspectionConfig) {
        String inspectManage = ActivityMenuEnum.BackendMenuEnum.RESULTS_MANAGE.getValue();
        ActivityMenuConfig inspectConfigMenu = activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
                .eq(ActivityMenuConfig::getMenu, inspectManage)).stream().findFirst().orElse(null);
        if (inspectConfigMenu == null) {
            activityMenuConfigMapper.insert(ActivityMenuConfig.builder()
                    .activityId(activityId)
                    .menu(inspectManage)
                    .dataOrigin(ActivityMenuConfig.DataOriginEnum.SYSTEM.getValue())
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

    /**更新菜单显示规则
     * @Description
     * @author huxiaolong
     * @Date 2022-01-18 16:40:55
     * @param menu
     * @return 返回menuId
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateMenuShowRule(ActivityMenuDTO menu) {
        Integer menuId = menu.getId();
        // menuId 为空仅出现在系统菜单或者模板菜单为添加的历史活动上
        if (menuId == null) {
            // 菜单id不存在，则需要新增活动菜单配置，仅仅活动菜单配置
            ActivityMenuConfig activityMenuConfig = ActivityMenuConfig.buildFromMenuDTO(menu);
            activityMenuConfigMapper.insert(activityMenuConfig);
            return activityMenuConfig.getId();
        }
        // 若菜单配置存在，则获取显示规则，判断菜单来源进行更新
        String showRule = StringUtils.isBlank(menu.getShowRule()) ? ActivityMenuConfig.ShowRuleEnum.NO_LIMIT.getValue() : menu.getShowRule();
        activityMenuConfigMapper.update(null, new LambdaUpdateWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getId, menuId)
                .set(ActivityMenuConfig::getShowRule, showRule));
        // 如果是活动自定义菜单，连同更新
        Integer activityMenuId = menu.getActivityMenuId();
        boolean isActivityDataOrigin = Objects.equals(ActivityMenuConfig.DataOriginEnum.ACTIVITY.getValue(), menu.getDataOrigin());
        if (activityMenuId != null && isActivityDataOrigin) {
            activityCustomAppConfigHandleService.updateMenuShowRule(activityMenuId, showRule);
        }
        return menuId;
    }

    /**更新菜单启用状态
     * @Description
     * @author huxiaolong
     * @Date 2022-01-18 16:29:13
     * @param menu
     * @return
     */
    public Integer updateMenuEnableStatus(ActivityMenuDTO menu) {
        Integer menuId = menu.getId();
        // menuId 为空仅出现在系统菜单或者模板菜单为历史的活动上
        if (menuId == null) {
            // 菜单id不存在，则需要新增活动菜单配置，仅仅活动菜单配置
            ActivityMenuConfig activityMenuConfig = ActivityMenuConfig.buildFromMenuDTO(menu);
            activityMenuConfigMapper.insert(activityMenuConfig);
            return activityMenuConfig.getId();
        }
        Boolean enable = Optional.ofNullable(menu.getEnable()).orElse(false);
        activityMenuConfigMapper.update(null, new LambdaUpdateWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getId, menuId)
                .set(ActivityMenuConfig::getEnable, enable));
        return menuId;
    }

    /**新增活动自定义菜单
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 11:26:06
     * @param menu
     * @return com.chaoxing.activity.dto.activity.ActivityMenuDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivityMenuDTO addActivityCustomMenu(ActivityMenuDTO menu) {
        // 新增活动自定义菜单
        ActivityCustomAppConfig activityCustomMenu = ActivityCustomAppConfig.buildFromMenuDTO(menu);
        activityCustomAppConfigHandleService.addActivityCustomMenu(activityCustomMenu);

        // 根据活动自定义菜单，拼接活动menuCode
        Integer activityMenuId = activityCustomMenu.getId();
        String menuCode = CommonConstant.ACTIVITY_MENU_PREFIX + activityMenuId;

        ActivityMenuConfig activityMenuConfig = ActivityMenuConfig.buildFromMenuDTO(menu);
        activityMenuConfig.setMenu(menuCode);
        // 新增菜单配置
        activityMenuConfigMapper.insert(activityMenuConfig);
        // 补充数据，用于返回页面显示
        Integer menuId = activityMenuConfig.getId();
        menu.setId(menuId);
        menu.setActivityMenuId(activityMenuId);
        menu.setEnable(true);
        return menu;
    }
    
    /**更新活动自定义菜单
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-19 15:15:58
     * @param menu
     * @return 
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityCustomMenu(ActivityMenuDTO menu) {
        if (menu == null) {
            return;
        }
        ActivityCustomAppConfig activityCustomMenu = ActivityCustomAppConfig.buildFromMenuDTO(menu);
        activityCustomAppConfigHandleService.updateMenu(activityCustomMenu);
    }

    /**移除活动自定义菜单
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 14:23:34
     * @param activityMenuId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeActivityMenu(Integer menuId, Integer activityMenuId) {
        // 活动自定义菜单删除
        activityCustomAppConfigHandleService.removeById(activityMenuId);
        // 活动菜单配置删除
        activityMenuConfigMapper.deleteById(menuId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMenusSort(List<ActivityMenuDTO> activityMenus) {
        AtomicInteger i = new AtomicInteger(1);
        activityMenus.forEach(v -> {
            Integer menuId = v.getId();
            v.setSequence(i.incrementAndGet());
            if (menuId == null) {
                // 菜单id不存在，则需要新增活动菜单配置，仅仅活动菜单配置
                /*ActivityMenuConfig activityMenuConfig = ActivityMenuConfig.buildFromMenuDTO(v);
                activityMenuConfigMapper.insert(activityMenuConfig);
                v.setId(activityMenuConfig.getId());*/
                return;
            }
            activityMenuConfigMapper.update(null, new LambdaUpdateWrapper<ActivityMenuConfig>()
                    .eq(ActivityMenuConfig::getId, menuId)
                    .set(ActivityMenuConfig::getSequence, v.getSequence()));
        });
    }
}
