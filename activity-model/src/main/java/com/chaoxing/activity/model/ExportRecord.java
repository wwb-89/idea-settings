package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 导出记录表
 * @className: ExportRecord, table_name: t_export_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-01 10:55:48
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_export_record")
public class ExportRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 导出类型; column: export_type*/
    private String exportType;
    /** 参数; column: params*/
    private String params;
    /** 文件名; column: file_name*/
    private String fileName;
    /** 文件大小（kb）; column: file_size*/
    private Long fileSize;
    /** 云盘资源id; column: cloud_id*/
    private String cloudId;
    /** 下载地址; column: download_url*/
    private String downloadUrl;
    /** 处理次数; column: handle_times*/
    private Integer handleTimes;
    /** 状态。0失败，1:成功，2:待处理; column: status*/
    private Integer status;
    /** 错误信息; column: message*/
    private String message;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 创建人fid; column: create_fid*/
    private Integer createFid;
    /** 创建人ip; column: create_ip*/
    private String createIp;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum ExportTypeEnum {

        /** 机构活动统计 */
        ORG_ACTIVITY_STAT("机构活动统计", "org_activity_stat"),
        ORG_USER_STAT("机构用户统计", "org_user_stat"),
        ACTIVITY_INSPECTION_MANAGE("活动成绩考核", "activity_inspection_manage");

        private final String name;
        private final String value;

        ExportTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static ExportTypeEnum fromValue(String value) {
            ExportTypeEnum[] values = ExportTypeEnum.values();
            for (ExportTypeEnum exportTypeEnum : values) {
                if (Objects.equals(exportTypeEnum.getValue(), value)) {
                    return exportTypeEnum;
                }
            }
            return null;
        }
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-06-01 15:09:08
    * @return
    */
    @Getter
    public enum StatusEnum {
        FAIL("失败", 0),
        SUCCESS("成功", 1),
        WAIT_HANDLE("待处理", 2);

        private String name;
        private Integer value;

        StatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static StatusEnum fromValue(Integer value) {
            StatusEnum[] values = StatusEnum.values();
            for (StatusEnum status : values) {
                if (Objects.equals(status.getValue(), value)) {
                    return status;
                }
            }
            return null;
        }
    }

}