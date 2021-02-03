package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.util.constant.UrlConstant;
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

	public static String generateAttachment(String title, String url) {
		return "{'attachmentType':25,'att_web':{'showContent':1,'logo':'" + UrlConstant.NOTICE_LOGO_URL + "','title':'" + title + "','content':'','url':'" + url + "'}}";
	}

}