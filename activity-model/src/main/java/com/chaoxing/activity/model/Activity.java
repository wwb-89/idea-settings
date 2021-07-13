package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.util.LocalDateTimeDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 活动表
 * @className: Activity, table_name: t_activity
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity")
public class Activity {

    /** 活动id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动名称; column: name*/
    private String name;
    /** 开始时间; column: start_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
    /** 结束时间; column: end_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    /** 开始日期; column: start_date*/
    private LocalDate startDate;
    /** 结束日期; column: end_date*/
    private LocalDate endDate;
    /** 封面云盘id; column: cover_cloud_id*/
    private String coverCloudId;
    /** 封面地址; column: cover_url*/
    private String coverUrl;
    /** 主办方; column: organisers*/
    private String organisers;
    /** 活动形式; column: activity_type*/
    private String activityType;
    /** 活动地址; column: address*/
    private String address;
    /** 详细地址; column: detail_address*/
    private String detailAddress;
    /** 经度; column: longitude*/
    private BigDecimal longitude;
    /** 维度; column: dimension*/
    private BigDecimal dimension;
    /** 活动分类id; column: activity_classify_id*/
    private Integer activityClassifyId;
    /** 学时; column: period*/
    private BigDecimal period;
    /** 学分; column: credit*/
    private BigDecimal credit;
    /** 参与时长上限（小时）; column: time_length_upper_limit*/
    private Integer timeLengthUpperLimit;
    /** 签到报名id; column: sign_id*/
    private Integer signId;
    /** 网页模板id; column: web_template_id*/
    private Integer webTemplateId;
    /** 门户网站id; column: website_id*/
    private Integer websiteId;
    /** 门户网页id; column: page_id*/
    private Integer pageId;
    /** 门户预览地址; column: preview_url*/
    private String previewUrl;
    /** 门户编辑地址; column: edit_url*/
    private String editUrl;
    /** 是否定时发布; column: is_timing_release*/
    @TableField(value = "is_timing_release")
    private Boolean timingRelease;
    /** 定时发布时间; column: timingReleaseTime*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime timingReleaseTime;
    /** 是否已发布; column: is_released*/
    @TableField(value = "is_released")
    private Boolean released;
    /** 发布时间; column: release_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime releaseTime;
    /** 发布人id; column: release_uid*/
    private Integer releaseUid;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 创建人姓名; column: create_user_name*/
    private String createUserName;
    /** 创建单位id; column: create_fid*/
    private Integer createFid;
    /** 创建区域编码; column: create_area_code*/
    private String createAreaCode;
    /** 创建机构名; column: create_org_name*/
    private String createOrgName;
    /** 标签。以逗号分隔; column: tags */
    private String tags;
    /** 是否开启评价; column: is_open_rating */
    @TableField(value = "is_open_rating")
    private Boolean openRating;
    /** 评价是否需要审核; column: is_rating_need_audit */
    @TableField(value = "is_rating_need_audit")
    private Boolean ratingNeedAudit;
    /** 积分值; column: integral*/
    private BigDecimal integral;
    /** 活动标识; column: activity_flag*/
    private String activityFlag;
    /** 是否开启作品征集; column: is_open_work*/
    @TableField(value = "is_open_work")
    private Boolean openWork;
    /** 作品征集id; column: work_id*/
    private Integer workId;
    /** 来源类型; column: origin_type*/
    private String originType;
    /** 来源值; column: origin*/
    private String origin;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 模版id; column: template_id*/
    private Integer templateId;
    /** 状态。0：已删除，1：待发布，2：已发布，3：进行中，4：已结束; column: status*/
    private Integer status;
    /** 创建时间; column: create_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    /** 修改时间; column: update_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    // 附加
    /** 活动详情 */
    @TableField(exist = false)
    private String introduction;
    /** 开始时间字符串 */
    @TableField(exist = false)
    private String startTimeStr;
    /** 结束时间字符串 */
    @TableField(exist = false)
    private String endTimeStr;
    /** 活动发布时间字符串 */
    @TableField(exist = false)
    private String timingReleaseTimeStr;
    /** 活动分类名称 */
    @TableField(exist = false)
    private String activityClassifyName;
    /** 报名人数 */
    @TableField(exist = false)
    private Integer signedUpNum;
    /** 管理员uid列表 */
    @TableField(exist = false)
    private List<Integer> managerUids;
    /** 起止时间 */
    @TableField(exist = false)
    private String activityStartEndTime;

    @Getter
    public enum OriginTypeEnum {

        /** 通用 */
        NORMAL("通用", "normal"),
        ACTIVITY_DECLARATION("活动申报", "activity_declaration");

        private final String name;
        private final String value;

        OriginTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static OriginTypeEnum fromValue(String value) {
            OriginTypeEnum[] values = OriginTypeEnum.values();
            for (OriginTypeEnum originTypeEnum : values) {
                if (Objects.equals(originTypeEnum.getValue(), value)) {
                    return originTypeEnum;
                }
            }
            return null;
        }

    }

    @Getter
    public enum ActivityTypeEnum {

        /** 线上举办 */
        ONLINE("线上举办", "online"),
        OFFLINE("线下举办", "offline");

        private final String name;
        private final String value;

        ActivityTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static ActivityTypeEnum fromValue(String value) {
            ActivityTypeEnum[] values = ActivityTypeEnum.values();
            for (ActivityTypeEnum activityTypeEnum : values) {
                if (Objects.equals(activityTypeEnum.getValue(), value)) {
                    return activityTypeEnum;
                }
            }
            return null;
        }

        public static ActivityTypeEnum fromName(String name) {
            ActivityTypeEnum[] values = ActivityTypeEnum.values();
            for (ActivityTypeEnum activityTypeEnum : values) {
                if (Objects.equals(activityTypeEnum.getName(), name)) {
                    return activityTypeEnum;
                }
            }
            return null;
        }

    }

    @Getter
    public enum StatusEnum {
        /** 已删除 */
        DELETED("已删除", 0),
        WAIT_RELEASE("未发布", 1),
        RELEASED("已发布", 2),
        ONGOING("进行中", 3),
        ENDED("已结束", 4);

        private final String name;
        private final Integer value;

        StatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static StatusEnum fromValue(Integer value) {
            StatusEnum[] values = StatusEnum.values();
            for (StatusEnum statusEnum : values) {
                if (Objects.equals(statusEnum.getValue(), value)) {
                    return statusEnum;
                }
            }
            throw new BusinessException("未知的活动状态");
        }
    }

    /**活动状态枚举
     * @className Activity
     * @description
     * @author wwb
     * @blame wwb
     * @date 2021-03-29 10:49:06
     * @version ver 1.0
     */
    @Getter
    public enum AuditStatusEnum {
        /** 已删除 */
        NOT_PASS("未通过", 0),
        PASSED("已通过", 1),
        WAIT_AUDIT("待审核", 2);

        private final String name;
        private final Integer value;

        AuditStatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static AuditStatusEnum fromValue(Integer value) {
            AuditStatusEnum[] values = AuditStatusEnum.values();
            for (AuditStatusEnum auditStatusEnum : values) {
                if (Objects.equals(auditStatusEnum.getValue(), value)) {
                    return auditStatusEnum;
                }
            }
            return null;
        }
    }

    /** 活动标示枚举
     * @className Activity
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-03-29 10:50:40
     * @version ver 1.0
     */
    @Getter
    public enum ActivityFlagEnum {

        /** 通用 */
        NORMAL("通用", "normal"),
        /** 第二课堂 */
        SECOND_CLASSROOM("第二课堂", "second_classroom"),
        /** 双选会 */
        DUAL_SELECT("双选会", "dual_select"),
        /** 教师发展 */
        TEACHER("教师发展", "teacher"),
        /** 志愿者服务 */
        VOLUNTEER("志愿者服务", "volunteer");

        private final String name;
        private final String value;

        ActivityFlagEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static ActivityFlagEnum fromValue(String value) {
            ActivityFlagEnum[] values = ActivityFlagEnum.values();
            for (ActivityFlagEnum activityFlag : values) {
                if (Objects.equals(activityFlag.getValue(), value)) {
                    return activityFlag;
                }
            }
            return null;
        }

    }

    public static Activity buildDefault() {
        return Activity.builder()
                .name("")
                .coverCloudId(CommonConstant.ACTIVITY_DEFAULT_COVER_CLOUD_ID)
                .organisers("")
                .address("")
                .detailAddress("")
                .openWork(false)
                .build();
    }

    public static String getStatusDescription(Integer status) {
        StatusEnum statusEnum = StatusEnum.fromValue(status);
        switch (statusEnum) {
            case DELETED:
                return "已删除";
            case WAIT_RELEASE:
            case RELEASED:
                return "未开始";
            case ONGOING:
                return "进行中";
            case ENDED:
                return "已结束";
            default:
                return "";
        }
    }

    /**发布
     * @Description 
     * @author wwb
     * @Date 2021-07-06 11:04:18
     * @param releaseUid
     * @return void
    */
    public void release(Integer releaseUid) {
        setReleased(true);
        setReleaseUid(releaseUid);
        LocalDateTime now = LocalDateTime.now();
        setReleaseTime(now);
    }

    /**取消发布
     * @Description 
     * @author wwb
     * @Date 2021-07-06 11:06:35
     * @param
     * @return void
    */
    public void cancelRelease() {
        setReleased(false);
        setReleaseUid(null);
        setReleaseTime(null);
    }

}