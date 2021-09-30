package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * 活动管理人员表
 * @className: ActivityManager, table_name: t_activity_manager
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-18 10:30:33
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_manager")
public class ActivityManager {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 用户姓名; column: user_name*/
    private String userName;
    /** 用户可见菜单; column: menu*/
    private String menu;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;

    /**创建者默认所有
    * @Description
    * @author huxiaolong
    * @Date 2021-09-28 14:15:51
    * @param activity
    * @return com.chaoxing.activity.model.ActivityManager
    */
    public static ActivityManager buildCreator(Activity activity) {
        return ActivityManager.builder()
                .activityId(activity.getId())
                .uid(activity.getCreateUid())
                .userName(activity.getCreateUserName())
                .menu(StringUtils.join(ActivityMenuDTO.listMenus(), ","))
                .build();
    }

}