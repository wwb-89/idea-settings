package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @className: TActivityStatTask, table_name: t_activity_stat_task
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_stat_task")
public class ActivityStatTask {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 任务日期; column: date*/
    private LocalDate date;
    /** 任务处理状态。0：失败，1:成功，2:待处理; column: status*/
    private Integer status;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

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

        public static Status fromValue(Integer value) {
            Status[] values = Status.values();
            for (Status status : values) {
                if (Objects.equals(status.getValue(), value)) {
                    return status;
                }
            }
            return null;
        }
    }

}