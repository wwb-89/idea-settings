package com.chaoxing.activity.dto.activity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.chaoxing.activity.util.LocalDateDeserializer;
import com.chaoxing.activity.util.LocalDateSerializer;
import com.chaoxing.activity.util.LocalDateTimeDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**外部使用的活动对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityExternalDTO
 * @description
 * @blame wwb
 * @date 2020-12-02 22:40:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityExternalDTO {

	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 开始日期 */
	@JSONField(serializeUsing = LocalDateSerializer.class, deserializeUsing = LocalDateDeserializer.class)
	private LocalDate startDate;
	/** 结束日期 */
	@JSONField(serializeUsing = LocalDateSerializer.class, deserializeUsing = LocalDateDeserializer.class)
	private LocalDate endDate;
	/** 封面云盘id */
	@JSONField(serialize = false)
	private String coverCloudId;
	/** 封面地址 */
	private String coverUrl;
	/** 活动形式 */
	private String activityType;
	/** 活动地址 */
	private String address;
	/** 经度 */
	@JSONField(serialize = false)
	private BigDecimal longitude;
	/** 维度 */
	@JSONField(serialize = false)
	private String dimension;
	/** 活动分类id */
	@JSONField(serialize = false)
	private Integer activityClassifyId;
	/** 活动分类 */
	private String activityClassify;
	/** 是否启用签到报名 */
	@JSONField(serialize = false)
	@TableField(value = "is_enable_sign")
	private Boolean enableSign;
	/** 签到报名id */
	@JSONField(serialize = false)
	private Integer signId;
	/** 网页模板id */
	@JSONField(serialize = false)
	private Integer webTemplateId;
	/** 门户网页id */
	@JSONField(serialize = false)
	private Integer pageId;
	/** 门户预览url */
	@JSONField(serialize = false)
	private String previewUrl;
	/** 门户编辑url */
	@JSONField(serialize = false)
	private Integer editUrl;
	/** 是否已发布 */
	@JSONField(serialize = false)
	@TableField(value = "is_released")
	private Boolean released;
	/** 发布时间 */
	@JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class, serialize = false)
	private LocalDateTime releaseTime;
	/** 发布人id */
	@JSONField(serialize = false)
	private Integer releaseUid;
	/** 状态。0：已删除，1：待发布，2：已发布，3：进行中，4：已结束 */
	@JSONField(serialize = false)
	private Integer status;
	/** 是否开启审核 */
	@JSONField(serialize = false)
	@TableField(value = "is_open_audit")
	private Boolean openAudit;
	/** 审核状态。0：审核不通过，1：审核通过，2：待审核 */
	@JSONField(serialize = false)
	private Integer auditStatus;
	/** 创建人id */
	private Integer createUid;
	/** 创建人姓名 */
	private String createUserName;
	/** 创建单位id */
	private Integer createFid;
	/** 创建机构名 */
	private String createOrgName;
	/** 创建时间 */
	@JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
	private LocalDateTime createTime;
	/** 修改时间 */
	@JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/** 访问地址 */
	private String url;

}