package com.chaoxing.activity.dto.notice;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.MarketNoticeTemplate;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**市场通知模版对象
 * @author wwb
 * @version ver 1.0
 * @className MarketNoticeTemplateDTO
 * @description
 * @blame wwb
 * @date 2021-11-11 14:29:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketNoticeTemplateDTO {

	/** 活动市场id */
	private Integer marketId;
	/** 通知类型 */
	private String noticeType;
	/** 通知类型描述 */
	private String noticeTypeDescription;
	/** 收件人描述 */
	private String receiverDescription;
	/** 显示的title */
	private String showTitle;
	/** 标题 */
	private String title;
	/** 标题（代码使用） */
	private String codeTitle;
	/** 显示的内容 */
	private String showContent;
	/** 内容 */
	private String content;
	/** 内容（代码使用） */
	private String codeContent;
	/** 发送时间描述 */
	private String sendTimeDescription;
	/** 是否支持时间配置 */
	private Boolean supportTimeConfig;
	/** 延迟小时数 */
	private Integer delayHour;
	/** 延迟分钟数 */
	private Integer delayMinute;
	/** 是否启用 */
	private Boolean enable;

	public static MarketNoticeTemplateDTO buildFromSystemNoticeTemplate(Integer marketId, SystemNoticeTemplate systemNoticeTemplate) {
		String noticeType = systemNoticeTemplate.getNoticeType();
		return MarketNoticeTemplateDTO.builder()
				.marketId(marketId)
				.noticeType(noticeType)
				.noticeTypeDescription(Optional.ofNullable(SystemNoticeTemplate.NoticeTypeEnum.fromValue(noticeType)).map(SystemNoticeTemplate.NoticeTypeEnum::getName).orElse(""))
				.receiverDescription(systemNoticeTemplate.getReceiverDescription())
				.title(systemNoticeTemplate.getTitle())
				.codeTitle(systemNoticeTemplate.getCodeTitle())
				.content(systemNoticeTemplate.getContent())
				.codeContent(systemNoticeTemplate.getCodeContent())
				.sendTimeDescription(systemNoticeTemplate.getSendTimeDescription())
				.supportTimeConfig(systemNoticeTemplate.getSupportTimeConfig())
				.delayHour(systemNoticeTemplate.getDelayHour())
				.delayMinute(systemNoticeTemplate.getDelayMinute())
				.enable(true)
				.build();
	}

	public MarketNoticeTemplate buildMarketNoticeTemplate() {
		return buildMarketNoticeTemplate(null);
	}

	public MarketNoticeTemplate buildMarketNoticeTemplate(MarketNoticeTemplate existMarketNoticeTemplate) {
		return MarketNoticeTemplate.builder()
				.id(Optional.ofNullable(existMarketNoticeTemplate).map(MarketNoticeTemplate::getId).orElse(null))
				.marketId(getMarketId())
				.noticeType(getNoticeType())
				.receiverDescription(getReceiverDescription())
				.title(getTitle())
				.codeTitle(getCodeTitle())
				.content(getContent())
				.codeContent(getCodeContent())
				.sendTimeDescription(getSendTimeDescription())
				.supportTimeConfig(getSupportTimeConfig())
				.delayHour(getDelayHour())
				.delayMinute(getDelayMinute())
				.enable(getEnable())
				.build();
	}

	/**处理显示的value
	 * @Description 在通知模版的管理表格中显示的title和content需要特殊处理
	 * @author wwb
	 * @Date 2021-11-11 16:17:40
	 * @param 
	 * @return void
	*/
	public void handleShowValue() {
		setShowTitle(convert2TableField(getCodeTitle()));
		setShowContent(convert2TableField(getCodeContent()));
	}

	private String convert2TableField(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		// 根据通知字段处理
		SystemNoticeTemplate.NoticeFieldEnum[] values = SystemNoticeTemplate.NoticeFieldEnum.values();
		for (SystemNoticeTemplate.NoticeFieldEnum noticeFieldEnum : values) {
			value = value.replaceAll(noticeFieldEnum.getValue(), noticeFieldEnum.getName());
		}
		return value;
	}

	/**获取延迟时间阈值
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-12 16:46:15
	 * @param
	 * @return long
	 */
	public long getDelayTimeThreshold() {
		Integer delayHour = Optional.ofNullable(getDelayHour()).orElse(0);
		Integer delayMinute = Optional.ofNullable(getDelayMinute()).orElse(0);
		return (delayHour * 60 * 60 + delayMinute * 60) * 1000L;
	}
}