package com.chaoxing.activity.dto.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**导出数据对象
 * @author wwb
 * @version ver 1.0
 * @className ExportDTO
 * @description
 * @blame wwb
 * @date 2021-04-14 10:25:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportDataDTO {

	/** 文件名 */
	private String fileName;
	/** sheet名 */
	private String sheetName;
	/** 导出excel的header */
	private List<List<String>> headers;
	/** 导出的数据 */
	private List<List<String>> data;

}