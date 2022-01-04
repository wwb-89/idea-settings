package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.CustomAppConfigMapper;
import com.chaoxing.activity.mapper.CustomAppEnableMapper;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.model.CustomAppEnable;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
     * @param type
     * @param customAppTplComponentIds
     * @return
     */
    public List<CustomAppConfig> list(String type, List<Integer> customAppTplComponentIds) {
        if (CollectionUtils.isEmpty(customAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return customAppConfigMapper.listCustomAppConfigWithCloudId(type, customAppTplComponentIds);
    }

    /**根据活动id查询后台应用配置列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-04 15:40:06
     * @param activityId
     * @return 
     */
    public List<CustomAppConfig> listBackendAppConfigsByActivity(Integer activityId) {
        List<Integer> enableCustomAppTplComponentIds = listEnabledActivityCustomAppTplComponentId(activityId);
        if (CollectionUtils.isEmpty(enableCustomAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return list(CustomAppConfig.UrlTypeEnum.BACKEND.getValue(), enableCustomAppTplComponentIds);

    }

    /**根据活动id查询前台应用配置列表
     * @Description
     * @author huxiaolong
     * @Date 2022-01-04 15:40:06
     * @param activityId
     * @return
     */
    public List<CustomAppConfig> listFrontendAppConfigsByActivity(Integer activityId) {
        List<Integer> enableCustomAppTplComponentIds = listEnabledActivityCustomAppTplComponentId(activityId);
        if (CollectionUtils.isEmpty(enableCustomAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return list(CustomAppConfig.UrlTypeEnum.FRONTEND.getValue(), enableCustomAppTplComponentIds);

    }
}
