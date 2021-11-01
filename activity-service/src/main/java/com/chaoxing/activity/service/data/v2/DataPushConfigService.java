package com.chaoxing.activity.service.data.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.DataPushConfigMapper;
import com.chaoxing.activity.mapper.DataPushFormConfigMapper;
import com.chaoxing.activity.model.DataPushConfig;
import com.chaoxing.activity.model.DataPushFormConfig;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**数据推送配置服务
 * @author wwb
 * @version ver 1.0
 * @className DataPushConfigService
 * @description
 * @blame wwb
 * @date 2021-11-01 15:09:09
 */
@Slf4j
@Service
public class DataPushConfigService {

    @Resource
    private DataPushConfigMapper dataPushConfigMapper;
    @Resource
    private DataPushFormConfigMapper dataPushFormConfigMapper;

    /**获取活动市场下配置的数据推送
     * @Description 
     * @author wwb
     * @Date 2021-11-01 15:12:48
     * @param marketId
     * @return java.util.List<com.chaoxing.activity.model.DataPushConfig>
    */
    public List<DataPushConfig> ListByMarketId(Integer marketId) {
        return dataPushConfigMapper.selectList(new LambdaQueryWrapper<DataPushConfig>()
                .eq(DataPushConfig::getMarketId, marketId)
                .eq(DataPushConfig::getEnable, true)
                .eq(DataPushConfig::getDeleted, false)
        );
    }

    /**获取活动市场下配置的数据推送
     * @Description 
     * @author wwb
     * @Date 2021-11-01 15:13:57
     * @param marketId
     * @param dataType
     * @return java.util.List<com.chaoxing.activity.model.DataPushConfig>
    */
    public List<DataPushConfig> ListByMarketId(Integer marketId, DataPushConfig.DataTypeEnum dataType) {
        return dataPushConfigMapper.selectList(new LambdaQueryWrapper<DataPushConfig>()
                .eq(DataPushConfig::getMarketId, marketId)
                .eq(DataPushConfig::getDataType, dataType.getValue())
                .eq(DataPushConfig::getEnable, true)
                .eq(DataPushConfig::getDeleted, false)
        );
    }

    /**根据id查询数据推送配置
     * @Description 
     * @author wwb
     * @Date 2021-11-01 15:15:33
     * @param id
     * @return com.chaoxing.activity.model.DataPushConfig
    */
    public DataPushConfig getById(Integer id) {
        List<DataPushConfig> dataPushConfigs = dataPushConfigMapper.selectList(new LambdaQueryWrapper<DataPushConfig>()
                .eq(DataPushConfig::getId, id)
                .eq(DataPushConfig::getEnable, true)
                .eq(DataPushConfig::getDeleted, false)
        );
        return Optional.ofNullable(dataPushConfigs).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**根据数据推送配置id查询表单配置详情列表
     * @Description 
     * @author wwb
     * @Date 2021-11-01 15:18:35
     * @param configId
     * @return java.util.List<com.chaoxing.activity.model.DataPushFormConfig>
    */
    public List<DataPushFormConfig> listFormConfigByConfigId(Integer configId) {
        return dataPushFormConfigMapper.selectList(new LambdaQueryWrapper<DataPushFormConfig>()
                .eq(DataPushFormConfig::getConfigId, configId)
        );

    }

}