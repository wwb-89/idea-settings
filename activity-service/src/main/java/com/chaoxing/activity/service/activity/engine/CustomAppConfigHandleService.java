package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.CustomAppConfigMapper;
import com.chaoxing.activity.mapper.CustomAppEnableMapper;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.model.CustomAppEnable;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**模板自定义应用配置服务
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/23 12:37 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class CustomAppConfigHandleService {

    @Autowired
    private CustomAppConfigMapper customAppConfigMapper;
    @Autowired
    private CustomAppEnableMapper customAppEnableMapper;

    /**新增
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:50:20
     * @param customAppConfigs
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(List<CustomAppConfig> customAppConfigs) {
        if (CollectionUtils.isEmpty(customAppConfigs)) {
            return;
        }
        customAppConfigMapper.batchAdd(customAppConfigs);
    }
    /**更新应用配置的模板组件id
     * @Description
     * @author huxiaolong
     * @Date 2021-12-30 16:47:32
     * @param templateComponent
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAppConfigTplComponentId(TemplateComponent templateComponent) {
        if (templateComponent.getComponentId() == null) {
            return;
        }
        customAppConfigMapper.update(null, new LambdaUpdateWrapper<CustomAppConfig>()
                .eq(CustomAppConfig::getComponentId, templateComponent.getComponentId())
                .set(CustomAppConfig::getTemplateComponentId, templateComponent.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateComponentCustomAppConfigs(List<Integer> removeConfigIds, List<CustomAppConfig> customAppConfigs) {
        // 批量删除已移除的配置id
        ApplicationContextHolder.getBean(CustomAppConfigHandleService.class).batchRemoveByIds(removeConfigIds);

        if (CollectionUtils.isEmpty(customAppConfigs)) {
            return;
        }
        // 新增
        List<CustomAppConfig> waitAddAppConfigs = customAppConfigs.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(waitAddAppConfigs)) {
            ApplicationContextHolder.getBean(CustomAppConfigHandleService.class).add(waitAddAppConfigs);
        }
        // 更新
        List<CustomAppConfig> waitUpdateAppConfigs = customAppConfigs.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(waitUpdateAppConfigs)) {
            waitUpdateAppConfigs.forEach( v -> customAppConfigMapper.updateById(v));
        }
    }

    /**根据配置id批量移除
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:46:12
     * @param configIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveByIds(List<Integer> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return;
        }
        customAppConfigMapper.update(null, new LambdaUpdateWrapper<CustomAppConfig>()
                .in(CustomAppConfig::getId, configIds)
                .set(CustomAppConfig::getDeleted, true));
    }

    /**保存活动关联的自定义应用启用列表
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-30 19:05:17
     * @param activityId
     * @param customAppEnableTplComponentIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityCustomAppEnables(Integer activityId, List<Integer> customAppEnableTplComponentIds) {
        // 先删除所有关联
        customAppEnableMapper.delete(new LambdaUpdateWrapper<CustomAppEnable>().eq(CustomAppEnable::getActivityId, activityId));

        if (CollectionUtils.isEmpty(customAppEnableTplComponentIds)) {
            return;
        }
        customAppEnableMapper.batchAdd(customAppEnableTplComponentIds
                .stream().map(v -> CustomAppEnable.builder()
                        .activityId(activityId)
                        .templateComponentId(v)
                        .build())
                .collect(Collectors.toList()));
    }
}
