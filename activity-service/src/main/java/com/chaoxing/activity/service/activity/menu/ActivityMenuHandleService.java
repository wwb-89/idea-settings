package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.mapper.ActivityMenuConfigMapper;
import com.chaoxing.activity.model.ActivityMenuConfig;
import com.chaoxing.activity.service.activity.engine.CustomAppConfigQueryService;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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
    private ActivityCustomAppConfigHandleService activityCustomAppConfigHandleService;
    @Resource
    private ActivityMenuQueryService activityMenuQueryService;
    @Resource
    private CustomAppConfigQueryService customAppConfigQueryService;


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


    /**活动菜单配置
     * @Description
     * @author huxiaolong
     * @Date 2022-01-11 11:12:20
     * @param activityId
     * @param menuList
     * @return
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void configActivityMenu(Integer activityId, List<String> menuList) {
        // 查询活动现有的活动菜单配置
        Map<String, ActivityMenuConfig> menuConfigMap = activityMenuQueryService.listByActivityId(activityId).stream().collect(Collectors.toMap(ActivityMenuConfig::getMenu, v -> v, (v1, v2) -> v2));
        List<ActivityMenuConfig> waitAddConfigs = Lists.newArrayList();
        List<ActivityMenuConfig> waitUpdateConfigs = Lists.newArrayList();
        menuList.forEach(menu -> {
            ActivityMenuConfig menuConfig = menuConfigMap.get(menu);
            if (menuConfig == null) {
                menuConfig = ActivityMenuConfig.builder().menu(menu).activityId(activityId).enable(true).build();
                ActivityMenuEnum.BackendMenuEnum backendMenuEnum = ActivityMenuEnum.BackendMenuEnum.fromValue(menu);
                if (backendMenuEnum == null) {
                    Integer tplComponentId = customAppConfigQueryService.getCustomAppTplComponentId(menu);
                    menuConfig.setSystem(false);
                    menuConfig.setTemplateComponentId(tplComponentId);
                    menuConfig.setSequence(150);
                } else {
                    menuConfig.setSystem(true);
                    menuConfig.setSequence(backendMenuEnum.getSequence());
                }
                waitAddConfigs.add(menuConfig);
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

    /**更新菜单显示规则
     * @Description
     * @author huxiaolong
     * @Date 2022-01-18 16:40:55
     * @param activityId
     * @param activityMenuDTO
     * @return
     */
    public void updateMenuShowRule(Integer activityId, ActivityMenuDTO activityMenuDTO) {

    }

    /**更新菜单启用状态
     * @Description
     * @author huxiaolong
     * @Date 2022-01-18 16:29:13
     * @param activityId
     * @param activityMenu
     * @return
     */
    public void updateMenuEnableStatus(Integer activityId, ActivityMenuDTO activityMenu) {

    }



}
