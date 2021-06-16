package com.chaoxing.activity.dto.manager.mh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**门户克隆结果对象
 * @author wwb
 * @version ver 1.0
 * @className MhCloneResultDTO
 * @description
 * @blame wwb
 * @date 2020-12-09 19:44:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MhCloneResultDTO {

	/** 网页id */
	private Integer pageId;
	/** 预览url */
	private String previewUrl;
	/** 编辑url */
	private String editUrl;

}
