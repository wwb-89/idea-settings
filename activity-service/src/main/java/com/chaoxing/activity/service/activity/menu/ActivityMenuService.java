package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.mapper.ActivityMenuConfigMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityMenuConfig;
import com.chaoxing.activity.util.enums.ActivityMenuEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
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
    private ActivityMenuConfigMapper activityMenuConfigMapper;

    /**查询菜单列表
     * @Description 
     * @author wwb
     * @Date 2021-08-06 16:11:08
     * @param 
     * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityMenuDTO>
    */
    public List<ActivityMenuDTO> listMenu() {
        return ActivityMenuDTO.list();
    }

    /**查询活动的菜单配置
     * @Description 
     * @author wwb
     * @Date 2021-08-06 16:12:38
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.ActivityMenuConfig>
    */
    public List<ActivityMenuConfig> listActivityMenuConfig(Integer activityId) {
        return activityMenuConfigMapper.selectList(new LambdaQueryWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
        );
    }

    /**查询活动的菜单配置
     * @Description
     * @author wwb
     * @Date 2021-08-06 16:12:38
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.ActivityMenuConfig>
    */
    public List<ActivityMenuDTO> listMenus(Integer activityId) {
        List<String> activityMenus = listActivityMenuConfig(activityId).stream().map(ActivityMenuConfig::getMenu).collect(Collectors.toList());
        return ActivityMenuDTO.buildFromActivityMenus(activityMenus);

    }

    /**配置活动的菜单
     * @Description 
     * @author wwb
     * @Date 2021-08-06 16:13:37
     * @param activityId
     * @param menus
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void configActivityMenu(Integer activityId, List<String> menus) {
        activityMenuConfigMapper.delete(new LambdaUpdateWrapper<ActivityMenuConfig>()
                .eq(ActivityMenuConfig::getActivityId, activityId)
        );
        if (CollectionUtils.isNotEmpty(menus)) {
            List<ActivityMenuConfig> activityMenuConfigs = menus.stream().map(v -> ActivityMenuConfig.builder().activityId(activityId).menu(v).build()).collect(Collectors.toList());
            activityMenuConfigMapper.batchAdd(activityMenuConfigs);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateActivityMenusByInspectionConfig(Integer activityId, boolean openInspectionConfig) {
        List<String> existMenus = listMenus(activityId).stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList());
        String inspectManage = ActivityMenuEnum.RESULTS_MANAGE.getValue();
        if (openInspectionConfig && !existMenus.contains(inspectManage)) {
            // 开启考核配置，添加默认考核管理菜单勾选
            existMenus.add(inspectManage);
            configActivityMenu(activityId, existMenus);
        } else if (!openInspectionConfig && existMenus.contains(inspectManage)) {
            // 未开启考核配置，关闭默认考核管理菜单勾选
            existMenus.remove(inspectManage);
            configActivityMenu(activityId, existMenus);
        }
    }


}