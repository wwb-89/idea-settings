package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.CustomAppInterfaceCallMapper;
import com.chaoxing.activity.mapper.CustomAppInterfaceCallRecordMapper;
import com.chaoxing.activity.model.CustomAppConfig;
import com.chaoxing.activity.model.CustomAppInterfaceCall;
import com.chaoxing.activity.model.CustomAppInterfaceCallRecord;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**自定义应用接口调用处理service
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/15 11:33 AM
 * @version: 1.0
 */
@Slf4j
@Service
public class CustomAppInterfaceCallHandleService {


    @Resource
    private CustomAppInterfaceCallMapper customAppInterfaceCallMapper;
    @Resource
    private CustomAppInterfaceCallRecordMapper customAppInterfaceCallRecordMapper;


    /**新增
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 16:00:28
     * @param customAppInterfaceCalls
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(List<CustomAppInterfaceCall> customAppInterfaceCalls) {
        if (CollectionUtils.isEmpty(customAppInterfaceCalls)) {
            return;
        }
        customAppInterfaceCallMapper.batchAdd(customAppInterfaceCalls);
    }

    /**更新自定义应用组件关联的自定义接口
     * @Description 
     * @author huxiaolong
     * @Date 2022-02-15 16:09:01
     * @param removeInterfaceCallIds
     * @param customAppInterfaceCalls
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateComponentInterfaceCalls(List<Integer> removeInterfaceCallIds, List<CustomAppInterfaceCall> customAppInterfaceCalls) {
        // 批量删除已移除的配置id
        ApplicationContextHolder.getBean(CustomAppInterfaceCallHandleService.class).batchRemoveByIds(removeInterfaceCallIds);

        if (CollectionUtils.isEmpty(customAppInterfaceCalls)) {
            return;
        }
        // 新增
        List<CustomAppInterfaceCall> waitAddAppConfigs = customAppInterfaceCalls.stream().filter(v -> v.getId() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(waitAddAppConfigs)) {
            ApplicationContextHolder.getBean(CustomAppInterfaceCallHandleService.class).add(waitAddAppConfigs);
        }
        // 更新
        List<CustomAppInterfaceCall> waitUpdateAppConfigs = customAppInterfaceCalls.stream().filter(v -> v.getId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(waitUpdateAppConfigs)) {
            waitUpdateAppConfigs.forEach( v -> customAppInterfaceCallMapper.updateById(v));
        }
    }

    /**根据id批量删除（硬删除）
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 16:10:28
     * @param removeInterfaceCallIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveByIds(List<Integer> removeInterfaceCallIds) {
        if (CollectionUtils.isEmpty(removeInterfaceCallIds)) {
            return;
        }
        customAppInterfaceCallMapper.deleteBatchIds(removeInterfaceCallIds);
    }

    /**更新自定义接口中缺失的templateComponentId
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 16:13:18
     * @param templateComponent
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInterfaceCallTplComponentId(TemplateComponent templateComponent) {
        if (templateComponent.getComponentId() == null) {
            return;
        }
        customAppInterfaceCallMapper.update(null, new LambdaUpdateWrapper<CustomAppInterfaceCall>()
                .eq(CustomAppInterfaceCall::getComponentId, templateComponent.getComponentId())
                .set(CustomAppInterfaceCall::getTemplateComponentId, templateComponent.getId()));
    }


    /**保存调用信息记录
     * @Description
     * @author huxiaolong
     * @Date 2022-02-15 14:43:49
     * @param record
     * @return
     */
    public void saveCallRecord(CustomAppInterfaceCallRecord record) {
        customAppInterfaceCallRecordMapper.insert(record);
    }



}
