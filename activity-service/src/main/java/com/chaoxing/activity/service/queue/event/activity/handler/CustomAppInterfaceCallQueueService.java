package com.chaoxing.activity.service.queue.event.activity.handler;

import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.engine.CustomAppInterfaceCallHandleService;
import com.chaoxing.activity.service.activity.engine.CustomAppInterfaceCallQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.queue.event.activity.CustomAppInterfaceCallQueue;
import com.chaoxing.activity.util.UrlUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**自定义应用接口调用任务服务
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/15 2:11 PM
 * @version: 1.0
 */
@Slf4j
@Service
public class CustomAppInterfaceCallQueueService {

    @Resource
    private CustomAppInterfaceCallQueue customAppInterfaceCallQueue;
    @Resource
    private CustomAppInterfaceCallQueryService customAppInterfaceCallQueryService;
    @Resource
    private CustomAppInterfaceCallHandleService customAppInterfaceCallHandleService;
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private RestTemplate restTemplate;

    /**接口调用处理
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 14:37:01
     * @param queueParam
     * @return
     */
    public void handle(CustomAppInterfaceCallQueue.QueueParamDTO queueParam) {
        Integer activityId = queueParam.getActivityId();
        Integer fid = queueParam.getFid();
        Integer interfaceCallId = queueParam.getInterfaceCallId();
        CustomAppInterfaceCall interfaceCall = customAppInterfaceCallQueryService.getById(interfaceCallId);
        CustomAppInterfaceCallRecord record = CustomAppInterfaceCallRecord.builder()
                .activityId(activityId)
                .templateComponentId(Optional.ofNullable(interfaceCall).map(CustomAppInterfaceCall::getTemplateComponentId).orElse(null))
                .status(1)
                .callTime(LocalDateTime.now())
                .build();;
        try {
            String url = Optional.ofNullable(interfaceCall).map(CustomAppInterfaceCall::getUrl).orElse(null);
            if (StringUtils.isBlank(url)) {
                throw new BusinessException("自定义应用接口:" + interfaceCallId + "不存在!");
            }
            Map<String, Object> additionalParams = Maps.newHashMap();
            additionalParams.put("activityId", String.valueOf(activityId));
            additionalParams.put("state", String.valueOf(fid));
            url = UrlUtils.packageParam2URL(url, additionalParams);
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            // 设置失败状态及异常信息
            record.setStatus(0);
            record.setMessage(e.getMessage());
        }
        customAppInterfaceCallHandleService.saveCallRecord(record);

    }

    /**接口调用
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 14:50:05
     * @param activity
     * @param callTimingEnum
     * @return
     */
    public void interfaceCall(Activity activity, Integer fid, CustomAppInterfaceCall.CallTimingEnum callTimingEnum) {
        fid = Optional.ofNullable(fid).orElse(activity.getCreateFid());
        Integer templateId = activity.getTemplateId();
        // 查询活动模板对应的自定义应用模板组件id列表
        List<Integer> customAppTplComponentIds = templateComponentService.listAllCustomTplComponent(templateId)
                .stream()
                .filter(v -> Objects.equals(v.getType(), Component.TypeEnum.CUSTOM_APP.getValue()))
                .map(TemplateComponent::getId)
                .collect(Collectors.toList());
        // 查询自定义应用对应调用时机callTimingEnum的接口调用配置
        List<CustomAppInterfaceCall> interfaceCalls = customAppInterfaceCallQueryService.listInterfaceCall(callTimingEnum, customAppTplComponentIds);
        if (CollectionUtils.isEmpty(interfaceCalls)) {
            return;
        }
        Integer activityId = activity.getId();
        // 将对应时机的接口调用放入任务队列等待执行
        Integer finalFid = fid;
        interfaceCalls.forEach(v -> {
            customAppInterfaceCallQueue.push(CustomAppInterfaceCallQueue.QueueParamDTO.builder()
                    .activityId(activityId)
                    .fid(finalFid)
                    .interfaceCallId(v.getId())
                    .build());
        });
    }
}
