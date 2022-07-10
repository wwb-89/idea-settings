package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.TemplatePushReminderConfigMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.TemplatePushReminderConfig;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**模板中消息推送组件配置服务
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/28 12:11 下午
 * @version: 1.0
 */
@Service
public class TemplatePushReminderConfigService {

    @Resource
    private TemplatePushReminderConfigMapper templatePushReminderConfigMapper;
    @Resource
    private TemplateComponentService templateComponentService;

    /**根据模板查询模板的消息推送组件配置
     * @Description
     * @author huxiaolong
     * @Date 2022-02-28 12:16:03
     * @param templateId
     * @return
     */
    public TemplatePushReminderConfig getByTemplateId(Integer templateId) {
        // 查询模板中组件消息推送的模板组件id
        Integer tplComponentId = templateComponentService.getSysComponentTplComponentId(templateId, Component.SystemComponentCodeEnum.PUSH_REMINDER.getValue());
        if (tplComponentId == null) {
            return null;
        }
        return getByTplComponentId(tplComponentId);
    }
    public TemplatePushReminderConfig getByTplComponentId(Integer tplComponentId) {
        return templatePushReminderConfigMapper.selectList(new LambdaQueryWrapper<TemplatePushReminderConfig>()
                .eq(TemplatePushReminderConfig::getTemplateComponentId, tplComponentId))
                .stream().findFirst().orElse(null);
    }


    public void addOrUpdate(TemplatePushReminderConfig pushReminderConfig) {
        if (pushReminderConfig.getId() == null) {
            templatePushReminderConfigMapper.insert(pushReminderConfig);
        } else {
            templatePushReminderConfigMapper.updateById(pushReminderConfig);
        }
    }
}
