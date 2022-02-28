package com.chaoxing.activity.dto.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className NoticeDTO
 * @description
 * @blame wwb
 * @date 2020-12-15 14:15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {

	private Long id;
	/** 通知标题 */
	private String title;
	/** 通知内容 */
	private String content;
	/** 附件 */
	private String attachment;
	/** 发送者id */
	private Integer senderUid;
	/** 接收者id列表 */
	private List<Integer> receiverUids;
	/** 接收范围json */
	private List<Togen> togens;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Togen {
		/** 范围类型: 单位-14， 单位角色-13 */
		private Integer type;
		/** 机构id 或  机构id-角色id */
		private String id;
		/** 名称 */
		private String name;
		/** 附件 */
		private String ext;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AttachmentDTO {

		@Builder.Default
		private Integer attachmentType = 25;
		@JSONField(name = "att_web")
		private AttWebDTO attWeb;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class AttWebDTO {

			@Builder.Default
			private Integer showContent = 1;
			private String logo;
			private String title;
			private String content;
			private String url;
		}
	}

	public static String generateActivityAttachment(String title, String url) {
		List<AttachmentDTO> attachments = Lists.newArrayList();
		AttachmentDTO.AttWebDTO attWeb = AttachmentDTO.AttWebDTO.builder()
				.logo(UrlConstant.NOTICE_LOGO_URL)
				.title(title)
				.content("")
				.url(url)
				.build();
		AttachmentDTO attachment = AttachmentDTO.builder().attWeb(attWeb).build();
		attachments.add(attachment);
		return JSON.toJSONString(attachments);
	}

	public static String generateActivityCertificateAttachment(String title, String url) {
		List<AttachmentDTO> attachments = Lists.newArrayList();
		AttachmentDTO.AttWebDTO attWeb = AttachmentDTO.AttWebDTO.builder()
				.logo(UrlConstant.NOTICE_LOGO_URL)
				.title(title)
				.content("")
				.url(url)
				.build();
		AttachmentDTO attachment = AttachmentDTO.builder().attWeb(attWeb).build();
		attachments.add(attachment);
		return JSON.toJSONString(attachments);
	}

}