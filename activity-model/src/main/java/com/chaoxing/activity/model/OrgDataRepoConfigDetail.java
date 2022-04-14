package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * 机构数据仓库配置详情
 * @className: TOrgDataRepoConfigDetail, table_name: t_org_data_repo_config_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-19 11:09:56
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_data_repo_config_detail")
public class OrgDataRepoConfigDetail {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 数据推送配置id; column: config_id*/
    private Integer configId;
    /** 仓库类型。表单或其它; column: repo_type*/
    private String repoType;
    /** 仓库值。表单id或其它; column: repo*/
    private String repo;
    /** 数据类型; column: data_type*/
    private String dataType;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum RepoTypeEnum {

        /** 表单 */
        FORM("表单", "form");

        private String name;
        private String value;

        RepoTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static RepoTypeEnum fromValue(String value) {
            RepoTypeEnum[] values = RepoTypeEnum.values();
            for (RepoTypeEnum repoTypeEnum : values) {
                if (Objects.equals(repoTypeEnum.getValue(), value)) {
                    return repoTypeEnum;
                }
            }
            return null;
        }

    }

    @Getter
    public enum DataTypeEnum {

        /** 活动 */
        ACTIVITY("活动", "activity"),
        PARTICIPATE_TIME_LENGTH("参与时长", "participate_time_length"),
        USER_ACTIVITY_DATA("用户活动数据", "user_activity_data");

        private String name;
        private String value;

        DataTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static DataTypeEnum fromValue(DataTypeEnum value) {
            DataTypeEnum[] values = DataTypeEnum.values();
            for (DataTypeEnum dataTypeEnum : values) {
                if (Objects.equals(dataTypeEnum.getValue(), value)) {
                    return dataTypeEnum;
                }
            }
            return null;
        }

    }

}