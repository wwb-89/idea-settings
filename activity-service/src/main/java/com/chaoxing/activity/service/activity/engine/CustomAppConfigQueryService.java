package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.CustomAppConfigMapper;
import com.chaoxing.activity.mapper.CustomAppEnableMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.model.CustomAppEnable;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**模板自定义应用配置服务
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/23 12:37 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class CustomAppConfigQueryService {

    @Autowired
    private CustomAppEnableMapper customAppEnableMapper;
    @Autowired
    private CustomAppConfigMapper customAppConfigMapper;
    @Resource
    private TemplateComponentService templateComponentService;

    /**根据组件id查询自定义应用配置
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-30 17:13:02
     * @param componentId
     * @return
     */
    public List<CustomAppConfig> listByComponentId(Integer componentId) {
        return customAppConfigMapper.selectList(new LambdaQueryWrapper<CustomAppConfig>()
                .eq(CustomAppConfig::getComponentId, componentId)
                .eq(CustomAppConfig::getDeleted, false));
    }

    /**查询模板templateId下的自定义应用配置列表（用于活动主页，含有图标）
     * @Description
     * @author huxiaolong
     * @Date 2021-12-25 14:13:42
     * @param templateId
     * @return
     */
    public List<CustomAppConfig> listByTemplateId(Integer templateId) {
        if (templateId == null) {
            return Lists.newArrayList();
        }
        List<Integer> customAppConfigTplComponentIds = templateComponentService.listCustomTemplateComponent(templateId)
                .stream()
                .filter(v -> Objects.equals(v.getType(), Component.TypeEnum.CUSTOM_APP.getValue()))
                .map(TemplateComponent::getId).collect(Collectors.toList());
        return listByTplComponentIds(customAppConfigTplComponentIds);
    }
    
    /**列出活动已启用的自定义应用模板组件id列表
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-30 18:58:51
     * @param activityId
     * @return 
     */
    public List<Integer> listEnabledActivityCustomAppTplComponentId(Integer activityId) {
        List<CustomAppEnable> customAppEnables = customAppEnableMapper.selectList(new LambdaQueryWrapper<CustomAppEnable>().eq(CustomAppEnable::getActivityId, activityId));
        return customAppEnables.stream().map(CustomAppEnable::getTemplateComponentId).collect(Collectors.toList());
    }
    
    /**查询启用的自定义应用模板组件id列表下的自定义应用配置
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-30 19:32:02
     * @param enableCustomAppTplComponentIds
     * @return 
     */
    public List<CustomAppConfig> listByTplComponentIds(List<Integer> enableCustomAppTplComponentIds) {
        if (CollectionUtils.isEmpty(enableCustomAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return customAppConfigMapper.listCustomAppConfigWithCloudId(enableCustomAppTplComponentIds);
    }
}
