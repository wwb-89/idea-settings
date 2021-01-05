package com.chaoxing.activity.dto.mh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**门户克隆参数对象
 * @author wwb
 * @version ver 1.0
 * @className MhCloneParamDTO
 * @description
 * @blame wwb
 * @date 2020-11-24 15:34:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MhCloneParamDTO {

	private Integer templateId;
	/** 网站名称 */
	private String websiteName;
	private Integer wfwfid;
	private Integer uid;
	private Integer originPageId;
	private List<MhAppDTO> appList;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MhAppDTO {

		private String appName;
		private Integer dataType;
		private String dataUrl;
		private List<MhAppDataDTO> dataList;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MhAppDataDTO {

		private String title;
		private String url;
		private String coverUrl;
		private Integer pageType;

	}

}