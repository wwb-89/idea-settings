package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.CustomAppConfigMapper;
import com.chaoxing.activity.mapper.CustomAppEnableMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Resource
    private CustomAppEnableMapper customAppEnableMapper;
    @Resource
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
     * @param containDeleted  true：数据中含有已删除的， 若false，数据deleted 均为0
     * @param customAppTplComponentIds
     * @return
     */
    public List<CustomAppConfig> list(Boolean containDeleted, List<Integer> customAppTplComponentIds) {
        containDeleted = Optional.ofNullable(containDeleted).orElse(false);
        if (CollectionUtils.isEmpty(customAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return customAppConfigMapper.listCustomAppConfigWithCloudId(containDeleted, customAppTplComponentIds);
    }

    public List<CustomAppConfig> list(List<Integer> customAppTplComponentIds) {
        if (CollectionUtils.isEmpty(customAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return customAppConfigMapper.listCustomAppConfigWithCloudId(false, customAppTplComponentIds);
    }

    public List<CustomAppConfig> listWithDeleted(List<Integer> customAppTplComponentIds) {
        return list(true, customAppTplComponentIds);
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
        return list(enableCustomAppTplComponentIds).stream().filter(v -> Objects.equals(v.getType(), ActivityMenuConfig.UrlTypeEnum.FRONTEND.getValue())).collect(Collectors.toList());
    }

    /**获取菜单对应的模板组件id
     * @Description
     * @author huxiaolong
     * @Date 2022-01-13 18:32:49
     * @param menu
     * @return
     */
    public Integer getCustomAppTplComponentId(String menu) {
        if (StringUtils.isEmpty(menu)) {
            return null;
        }
        CustomAppConfig customAppConfig = customAppConfigMapper.selectList(new LambdaQueryWrapper<CustomAppConfig>().eq(CustomAppConfig::getId, Integer.valueOf(menu))).stream().findFirst().orElse(null);
        return Optional.ofNullable(customAppConfig).map(CustomAppConfig::getTemplateComponentId).orElse(null);
    }

    public List<CustomAppConfig> listByTemplateId(Integer templateId) {
        List<Integer> customAppTplComponentIds = templateComponentService.listAllCustomTplComponent(templateId)
                .stream()
                .filter(v -> Objects.equals(v.getType(), Component.TypeEnum.CUSTOM_APP.getValue()))
                .map(TemplateComponent::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(customAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        return list(customAppTplComponentIds);
    }
}
