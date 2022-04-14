package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityMenuConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityMenuConfigMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-08-06 16:01:41
 * @version: ver 1.0
 */
@Mapper
public interface ActivityMenuConfigMapper extends BaseMapper<ActivityMenuConfig> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-08-06 16:14:46
     * @param activityMenuConfigs
     * @return int
    */
    int batchAdd(@Param("activityMenuConfigs") List<ActivityMenuConfig> activityMenuConfigs);

}