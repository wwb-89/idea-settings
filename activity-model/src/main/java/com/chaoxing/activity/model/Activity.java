package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.util.LocalDateTimeDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private BigDecimal timeLengthUpperLimit;
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
    /** 是否开启阅读设置 */
    @TableField(value = "is_open_reading")
    private Boolean openReading;
    /** 阅读id */
    private Integer readingId;
    /** 阅读模块id */
    private Integer readingModuleId;
    /** 来源类型; column: origin_type*/
    private String originType;
    /** 来源值; column: origin*/
    private String origin;
    /** 来源值记录id; column: origin_form_user_id*/
    private Integer originFormUserId;
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
    /** 报名成功是否发送通知; column: is_signed_up_notice*/
    @TableField(value = "is_signed_up_notice")
    private Boolean signedUpNotice;
    /** 源活动id; column: origin_activity_id */
    private Integer originActivityId;
    /** 是否开启小组; column: is_open_group*/
    @TableField(value = "is_open_group")
    private Boolean openGroup;
    /** 小组bbsid; column: group_bbsid*/
    private String groupBbsid;
    /** 是否归档; column: is_archive*/
    @TableField(value = "is_archived")
    private Boolean archived;
    /** 是否开启班级互动; column: is_open_clazz_interaction */
    @TableField(value = "is_open_clazz_interaction")
    private Boolean openClazzInteraction;
    /** 班级id; column: clazz_id */
    private Integer clazzId;
    /** 课程id; column: course_id */
    private Integer courseId;
    /** 证书模版id; column: certificate_template_id */
    private Integer certificateTemplateId;
    /** 是否开启推送提醒 */
    @TableField(value = "is_open_push_reminder")
    private Boolean openPushReminder;

    // 附加
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
    /** 是否置顶 */
    @TableField(exist = false)
    private Boolean top;
    /** 签到率 */
    @TableField(exist = false)
    private BigDecimal signedInRate;
    /** 人数限制 */
    @TableField(exist = false)
    private Integer personLimit;
    /** 签到数 */
    @TableField(exist = false)
    private Integer signedInNum;
    /** 评价数 */
    @TableField(exist = false)
    private Integer rateNum;
    /** 活动评分 */
    @TableField(exist = false)
    private BigDecimal rateScore;
    /** 合格人数 */
    @TableField(exist = false)
    private Integer qualifiedNum;
    /** 是否有报名 */
    @TableField(exist = false)
    private Boolean hasSignUp = false;
    /** 是否开启报名 */
    @TableField(exist = false)
    private Boolean openSignUp;
    /** 报名状态 */
    @TableField(exist = false)
    private Integer signUpStatus;
    /** 报名状态描述 */
    @TableField(exist = false)
    private String signUpStatusDescribe;
    /** 标签名称列表 */
    @TableField(exist = false)
    private List<String> tagNames;
    @TableField(exist = false)
    private List<ActivityComponentValue> activityComponentValues;
    /** 标签 */
    @TableField(exist = false)
    private String tags;

    @Getter
    public enum OriginTypeEnum {

        /** 通用 */
        NORMAL("通用", "normal"),
        ACTIVITY_DECLARATION("活动申报", "activity_declaration"),
        WFW_FORM("万能表单", "wfw_form"),
        ACTIVITY_RELEASE("活动发布", "activity_release");

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
        OFFLINE("线下举办", "offline"),
        OTHER("线上+线下", "other");

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
        RELEASED("未开始", 2),
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
        NORMAL("活动市场", "normal"),
        /** 第二课堂 */
        SECOND_CLASSROOM("第二课堂活动", "second_classroom"),
        /** 双选会 */
        DUAL_SELECT("双选会", "dual_select"),
        /** 教师发展 */
        TEACHER("线下培训", "teacher"),
        /** 志愿者服务 */
        VOLUNTEER("志愿活动", "volunteer"),
        /** 三会一课 */
        THREE_CONFERENCE_ONE_LESSON("三会一课", "tcol"),
        /** 班级活动 */
        CLASS("班级活动", "class"),
        /** 学校活动 */
        SCHOOL("学校活动", "school"),
        /** 区域活动 */
        REGION("区域活动", "region"),
        PREACH_ONLINE("线上宣讲会", "preach_online"),
        PREACH_OFFLINE("线下宣讲会", "preach_offline"),
        ZJLIB("浙江省图书馆", "zjlib"),
        TRAINING("培训", "training"),
        SUBJECT("课题", "subject"),
        STUDIO("工作室", "studio");

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

    public void delete() {
        setStatus(StatusEnum.DELETED.getValue());
    }

    public void coverCloudIdChange(String cloudId) {
        if (!Objects.equals(cloudId, this.getCoverCloudId())) {
            setCoverUrl("");
        }
    }

    public void beforeCreate(Integer uid, String userName, Integer fid, String orgName) {
        setStatus(Activity.StatusEnum.WAIT_RELEASE.getValue());
        setReleased(false);
        setCreateUid(uid);
        setCreateUserName(userName);
        setCreateFid(fid);
        setCreateOrgName(orgName);
    }

    public boolean isEnded() {
        return Objects.equals(StatusEnum.ENDED.getValue(), getStatus());
    }

    public boolean isDeleted() {
        return Objects.equals(StatusEnum.DELETED.getValue(), getStatus());
    }

    /**计算活动状态
     * @Description
     * @author wwb
     * @Date 2020-12-10 19:36:50
     * @param activity
     * @return void
     */
    public static void calAndSetActivityStatus(Activity activity) {
        StatusEnum status = calAndSetActivityStatus(activity.getStartTime(), activity.getEndTime(), activity.getReleased());
        activity.setStatus(status.getValue());
    }

    /**计算活动状态
     * @Description 
     * @author wwb
     * @Date 2021-09-14 15:39:00
     * @param startTime
     * @param endTime
     * @param released
     * @return com.chaoxing.activity.model.Activity.StatusEnum
    */
    public static Activity.StatusEnum calAndSetActivityStatus(LocalDateTime startTime, LocalDateTime endTime, Boolean released) {
        LocalDateTime now = LocalDateTime.now();
        boolean guessEnded = now.isAfter(endTime);
        boolean guessOnGoing = (now.isAfter(startTime) || now.isEqual(startTime)) && (now.isBefore(endTime) || now.isEqual(endTime));
        if (released) {
            if (guessEnded) {
                // 已结束
                return Activity.StatusEnum.ENDED;
            }
            // 已发布的活动才处理状态
            if (guessOnGoing) {
                return Activity.StatusEnum.ONGOING;
            } else {
                return Activity.StatusEnum.RELEASED;
            }
        } else {
            return Activity.StatusEnum.WAIT_RELEASE;
        }
    }

    /**活动更新通过已经存在的活动完善一些信息
     * @Description 是否发布、发布时间在修改活动的时候是不能修改的
     * @author wwb
     * @Date 2021-09-09 14:30:50
     * @param existActivity
     * @return void
    */
    public void updatePerfectFromExistActivity(Activity existActivity) {
        setReleased(existActivity.getReleased());
        setReleaseTime(existActivity.getReleaseTime());
        setGroupBbsid(existActivity.getGroupBbsid());
        setMarketId(existActivity.getMarketId());
    }

    /**获取活动全地址(address + detailAddress)
     * @Description
     * @author huxiaolong
     * @Date 2021-11-12 17:11:26
     * @param
     * @return java.lang.String
     */
    public String getActivityFullAddress() {
        return Optional.ofNullable(getAddress()).orElse("") + Optional.ofNullable(getDetailAddress()).orElse("");
    }

    public String getAdminUrl() {
        return DomainConstant.ADMIN + "/activity/" + id;
    }

}