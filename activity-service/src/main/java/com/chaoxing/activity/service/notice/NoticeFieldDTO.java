package com.chaoxing.activity.service.notice;

import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**通知字段对象
 * @author wwb
 * @version ver 1.0
 * @className NoticeFieldDTO
 * @description
 * @blame wwb
 * @date 2021-11-11 15:15:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFieldDTO {

	/** 名称 */
	private String name;
	/** 值 */
	private String value;

	public static List<NoticeFieldDTO> buildFromNoticeFieldEnum() {
		List<NoticeFieldDTO> result = Lists.newArrayList();
		SystemNoticeTemplate.NoticeFieldEnum[] values = SystemNoticeTemplate.NoticeFieldEnum.values();
		for (SystemNoticeTemplate.NoticeFieldEnum noticeFieldEnum : values) {
			result.add(NoticeFieldDTO.builder()
					.name(noticeFieldEnum.getName())
					.value(noticeFieldEnum.getValue())
					.build());
		}
		return result;
	}

}
