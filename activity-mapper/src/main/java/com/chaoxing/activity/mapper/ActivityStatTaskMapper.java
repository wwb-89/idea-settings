package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityStatTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityStatTaskMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Mapper
public interface ActivityStatTaskMapper extends BaseMapper<ActivityStatTask> {

    /**批量添加任务
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-09 17:21:32
    * @param activityStatTasks
    * @return void
    */
    void batchAdd(@Param("activityStatTasks") List<ActivityStatTask> activityStatTasks);
}