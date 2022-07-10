package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityStatTask;
import com.chaoxing.activity.model.ActivityStatTaskDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityStatTaskDetailMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Mapper
public interface ActivityStatTaskDetailMapper extends BaseMapper<ActivityStatTaskDetail> {

    /**批量新增
    * @Description
    * @author huxiaolong
    * @Date 2021-05-10 17:34:28
    * @param activityStatTaskDetails
    * @return void
    */
    void batchAdd(@Param("activityStatTaskDetails") List<ActivityStatTaskDetail> activityStatTaskDetails);
}