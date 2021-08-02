package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityComponentValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityComponentValueMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:25
 * @version: ver 1.0
 */
@Mapper
public interface ActivityComponentValueMapper extends BaseMapper<ActivityComponentValue> {

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-19 15:46:45
    * @param activityComponentValues
    * @return void
    */
    void batchAdd(@Param("activityComponentValues") List<ActivityComponentValue> activityComponentValues);
}