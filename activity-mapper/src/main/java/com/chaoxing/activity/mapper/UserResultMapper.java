package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.UserResultDTO;
import com.chaoxing.activity.model.UserResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: UserResultMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-16 11:01:23
 * @version: ver 1.0
 */
@Mapper
public interface UserResultMapper extends BaseMapper<UserResult> {


    /**分页查询活动下用户的成绩
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-24 15:57:44
    * @param page
    * @param activityId
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.UserResultDTO>
    */
    Page<UserResultDTO> pageUserResult(@Param("page") Page<UserResultDTO> page, @Param("activityId") Integer activityId);

}