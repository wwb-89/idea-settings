package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Objects;

/**
 * 任务详情
 * @className: ActivityStatTaskDetail, table_name: t_activity_stat_task_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_stat_task_detail")
public class ActivityStatTaskDetail {

    /** 任务id; column: task_id*/
    @TableId(type = IdType.AUTO)
    private Integer taskId;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 失败次数; column: error_times*/
    private Integer errorTimes;
    /** 失败信息; column: error_message*/
    private String errorMessage;
    /** 处理状态。0：失败，1：成功，2：待处理; column: status*/
    private Integer status;

    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-05-10 16:35:13
     */
    @Getter
    public enum Status {

        /** 取消 */
        FAIL("失败", 0),
        SUCCESS("成功", 1),
        WAIT_HANDLE("待处理", 2);

        private String name;
        private Integer value;

        Status(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static ActivityStatTask.Status fromValue(Integer value) {
            ActivityStatTask.Status[] values = ActivityStatTask.Status.values();
            for (ActivityStatTask.Status status : values) {
                if (Objects.equals(status.getValue(), value)) {
                    return status;
                }
            }
            return null;
        }
    }

}