package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.TemplateCustomAppConfigMapper;
import com.chaoxing.activity.model.TemplateCustomAppConfig;
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
public class TemplateCustomAppConfigService {

    @Autowired
    private TemplateCustomAppConfigMapper templateCustomAppConfigMapper;

    public List<TemplateCustomAppConfig> listTemplateComponentFrontendAppConfigs(Integer templateComponentId) {
        return listTemplateComponentAppConfigsByType(templateComponentId, TemplateCustomAppConfig.UrlTypeEnum.FRONTEND);
    }

    public List<TemplateCustomAppConfig> listTemplateComponentBackendAppConfigs(Integer templateComponentId) {
        return listTemplateComponentAppConfigsByType(templateComponentId, TemplateCustomAppConfig.UrlTypeEnum.BACKEND);
    }

    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:43:44
     * @param templateComponentId
     * @param urlTypeEnum
     * @return
     */
    private List<TemplateCustomAppConfig> listTemplateComponentAppConfigsByType(Integer templateComponentId, TemplateCustomAppConfig.UrlTypeEnum urlTypeEnum) {
        return templateCustomAppConfigMapper.selectList(new LambdaQueryWrapper<TemplateCustomAppConfig>()
                .eq(TemplateCustomAppConfig::getTemplateComponentId, templateComponentId)
                .eq(TemplateCustomAppConfig::getType, urlTypeEnum.getValue())
                .eq(TemplateCustomAppConfig::getDeleted, false)
                .orderByAsc(TemplateCustomAppConfig::getSequence));
    }



    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:50:20
     * @param templateComponentId
     * @param templateCustomAppConfigs
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Integer templateComponentId, List<TemplateCustomAppConfig> templateCustomAppConfigs) {
        if (CollectionUtils.isEmpty(templateCustomAppConfigs)) {
            return;
        }
        templateCustomAppConfigs.forEach(v -> v.setTemplateComponentId(templateComponentId));
        templateCustomAppConfigMapper.batchAdd(templateCustomAppConfigs);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleTemplateCustomAppConfigs(Integer templateComponentId, List<Integer> deletedConfigIds, List<TemplateCustomAppConfig> templateCustomAppConfigs) {
        // 批量删除已移除的配置id
        ApplicationContextHolder.getBean(TemplateCustomAppConfigService.class).batchRemoveByIds(deletedConfigIds);

        if (CollectionUtils.isEmpty(templateCustomAppConfigs)) {
            return;
        }
        // 新增
        List<TemplateCustomAppConfig> waitAddAppConfigs = templateCustomAppConfigs.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
        ApplicationContextHolder.getBean(TemplateCustomAppConfigService.class).add(templateComponentId, waitAddAppConfigs);

        // 更新
        List<TemplateCustomAppConfig> waitUpdateAppConfigs = templateCustomAppConfigs.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(waitUpdateAppConfigs)) {
            waitAddAppConfigs.forEach( v -> templateCustomAppConfigMapper.updateById(v));
        }
    }

    /**根据配置id批量移除
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:46:12
     * @param configIds
     * @return
     */
    public void batchRemoveByIds(List<Integer> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return;
        }
        templateCustomAppConfigMapper.update(null, new LambdaUpdateWrapper<TemplateCustomAppConfig>()
                .in(TemplateCustomAppConfig::getId, configIds)
                .set(TemplateCustomAppConfig::getDeleted, true));
    }
}
