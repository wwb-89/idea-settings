package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityCreatePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityCreatePermissionMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-01 18:33:22
 * @version: ver 1.0
 */
@Mapper
public interface ActivityCreatePermissionMapper extends BaseMapper<ActivityCreatePermission> {

    /**批量增加权限
    * @Description
    * @author huxiaolong
    * @Date 2021-06-02 16:48:18
    * @param permissions
    * @return void
    */
    void batchAdd(@Param("permissions") List<ActivityCreatePermission> permissions);
}