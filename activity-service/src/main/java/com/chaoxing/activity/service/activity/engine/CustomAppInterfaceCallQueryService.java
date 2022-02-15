package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.CustomAppInterfaceCallMapper;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.model.CustomAppInterfaceCall;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**自定义应用接口调用查询service
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/15 11:33 AM
 * @version: 1.0
 */
@Slf4j
@Service
public class CustomAppInterfaceCallQueryService {

    @Resource
    private CustomAppInterfaceCallMapper customAppInterfaceCallMapper;

    /**根据id获取
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 14:19:21
     * @param interfaceCallId
     * @return
     */
    public CustomAppInterfaceCall getById(Integer interfaceCallId) {
        if (interfaceCallId == null) {
            return null;
        }
        return customAppInterfaceCallMapper.selectById(interfaceCallId);
    }

    /**根据组件id查询自定义接口
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 16:25:30
     * @param componentId
     * @return
     */
    public List<CustomAppInterfaceCall> listByComponentId(Integer componentId) {
        List<CustomAppInterfaceCall> customAppInterfaceCalls = customAppInterfaceCallMapper.selectList(new LambdaQueryWrapper<CustomAppInterfaceCall>().eq(CustomAppInterfaceCall::getComponentId, componentId));
        customAppInterfaceCalls.forEach(CustomAppInterfaceCall::statusValTransfer2CallTiming);
        return customAppInterfaceCalls;
    }

    /**根据接口调用时机类型、模板组件id列表，查询自定义应用接口调用列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-02-15 14:54:44
     * @param callTimingEnum
     * @return 
     */
    public List<CustomAppInterfaceCall> listInterfaceCall(CustomAppInterfaceCall.CallTimingEnum callTimingEnum, List<Integer> customAppTplComponentIds) {
        if (CollectionUtils.isEmpty(customAppTplComponentIds)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<CustomAppInterfaceCall> wrapper = new LambdaQueryWrapper<CustomAppInterfaceCall>().in(CustomAppInterfaceCall::getTemplateComponentId, customAppTplComponentIds);
        switch (callTimingEnum) {
            case CREATE_CALL:
                wrapper.eq(CustomAppInterfaceCall::getCreateCall, Boolean.TRUE);
                break;
            case RELEASE_CALL:
                wrapper.eq(CustomAppInterfaceCall::getReleaseCall, Boolean.TRUE);
                break;
            case CANCEL_RELEASE_CALL:
                wrapper.eq(CustomAppInterfaceCall::getCancelReleaseCall, Boolean.TRUE);
                break;
            case START_CALL:
                wrapper.eq(CustomAppInterfaceCall::getStartCall, Boolean.TRUE);
                break;
            case END_CALL:
                wrapper.eq(CustomAppInterfaceCall::getEndCall, Boolean.TRUE);
                break;
            case DELETE_CALL:
                wrapper.eq(CustomAppInterfaceCall::getDeleteCall, Boolean.TRUE);
                break;
            default:
                break;
        }
        return customAppInterfaceCallMapper.selectList(wrapper);
    }

}
