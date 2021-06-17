package com.chaoxing.activity.dto.manager.mh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**门户通用应用结果数据对象
 * @author wwb
 * @version ver 1.0
 * @className MhGeneralAppResultDataDTO
 * @description
 * @blame wwb
 * @date 2020-11-24 18:00:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MhGeneralAppResultDataDTO {

	/** 资源ID */
	private Integer id;
	/** 跳转方式，1使用门户详情页, 2搜索列表，3使用外部地址,4搜索详情" */
	private Integer type;
	/** 当前记录的跳转地址 */
	private String orsUrl;
	/** 资源的字段属性 */
	private List<MhGeneralAppResultDataFieldDTO> fields;
	/** 0否，1是【是否需要弹窗】 */
	private Integer pop;
	/** 弹窗详情内容地址 */
	private String popUrl;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MhGeneralAppResultDataFieldDTO {
		private String key;
		// 字段内容
		private String value;
		// 当前字段跳转地址
		private String orsUrl;
		private String flag;
		// 跳转方式，1使用门户详情页，2使用搜索列表， 3使用外部地址， 4搜索详情",  // 搜索类资源做外部数据源的时候，也需要该字段来区别,
		private String type;
	}

}