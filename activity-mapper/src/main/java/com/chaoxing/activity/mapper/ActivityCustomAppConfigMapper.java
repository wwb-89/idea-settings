package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityCustomAppConfig;
import com.chaoxing.activity.model.CustomAppConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityCustomAppConfigMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-01-17 09:33:22
 * @version: ver 1.0
 */
@Mapper
public interface ActivityCustomAppConfigMapper extends BaseMapper<ActivityCustomAppConfig> {


    /**查询活动的自定义应用配置列表（含有图标）
     * @Description
     * @author huxiaolong
     * @Date 2022-01-17 10:45:33
     * @param activityId
     * @param type
     * @return
     */
    List<ActivityCustomAppConfig> listActivityAppWithCloudId(@Param("activityId") Integer activityId, @Param("type") String type);
}