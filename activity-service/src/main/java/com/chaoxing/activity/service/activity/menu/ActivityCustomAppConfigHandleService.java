package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.ActivityCustomAppConfigMapper;
import com.chaoxing.activity.model.ActivityCustomAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Optional;

/**活动自定义菜单配置服务
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/17 10:55 上午
 * @version: 1.0
 */
@Slf4j
@Service
public class ActivityCustomAppConfigHandleService {

    @Resource
    private ActivityCustomAppConfigMapper activityCustomAppConfigMapper;

    /**新增活动自定义菜单
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 11:32:14
     * @param activityCustomAppConfig
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addActivityCustomMenu(ActivityCustomAppConfig activityCustomAppConfig) {
        activityCustomAppConfigMapper.insert(activityCustomAppConfig);
    }

    /**根据id移除活动自定义菜单
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 14:26:09
     * @param activityMenuId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer activityMenuId) {
        activityCustomAppConfigMapper.update(null, new LambdaUpdateWrapper<ActivityCustomAppConfig>()
                .eq(ActivityCustomAppConfig::getId, activityMenuId)
                .set(ActivityCustomAppConfig::getDeleted, true));
    }

    /**更新菜单显示规则
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 15:41:23
     * @param activityMenuId
     * @param showRule
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuShowRule(Integer activityMenuId, String showRule) {
        activityCustomAppConfigMapper.update(null, new LambdaUpdateWrapper<ActivityCustomAppConfig>()
                .eq(ActivityCustomAppConfig::getId, activityMenuId)
                .set(ActivityCustomAppConfig::getShowRule, showRule));
    }

    /**更新活动菜单
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-19 15:53:04
     * @param menu
     * @return 
     */
    public void updateMenu(ActivityCustomAppConfig menu) {
        Boolean pc = Optional.ofNullable(menu.getPc()).orElse(false);
        Boolean mobile = Optional.ofNullable(menu.getMobile()).orElse(false);
        Boolean openBlank = Optional.ofNullable(menu.getOpenBlank()).orElse(true);
        activityCustomAppConfigMapper.update(null, new LambdaUpdateWrapper<ActivityCustomAppConfig>()
                .eq(ActivityCustomAppConfig::getId, menu.getId())
                .set(ActivityCustomAppConfig::getTitle, menu.getTitle())
                .set(ActivityCustomAppConfig::getIconId, menu.getIconId())
                .set(ActivityCustomAppConfig::getUrl, menu.getUrl())
                .set(ActivityCustomAppConfig::getPc, pc)
                .set(ActivityCustomAppConfig::getMobile, mobile)
                .set(ActivityCustomAppConfig::getOpenBlank, openBlank));
    }
}
