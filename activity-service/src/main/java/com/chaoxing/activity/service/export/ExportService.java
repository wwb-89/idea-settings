package com.chaoxing.activity.service.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**导出服务
 * @className ExportService
 * @description 
 * @author huxiaolong
 * @blame wwb
 * @date 2021-06-28 14:56:48
 * @version ver 1.0
 */
@Slf4j
@Service
public class ExportService {
	static class ExcelWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {
		// 统计setColumnWidth被调用多少次
		private static int count = 0;

		@Override
		protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<CellData> cellDataList, Cell cell, Head head,
									  Integer relativeRowIndex, Boolean isHead) {
			// 简单设置
			Sheet sheet = writeSheetHolder.getSheet();
			sheet.setColumnWidth(cell.getColumnIndex(), 5000);
		}
	}

	public void export(ExportDataDTO exportData, HttpServletResponse response) throws IOException {
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("utf-8");
		// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
		String fileName = URLEncoder.encode(exportData.getFileName(), StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
		response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
		// 头的策略
		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		// 背景设置为红色
		headWriteCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setColor(IndexedColors.WHITE.getIndex());
		headWriteCellStyle.setWriteFont(headWriteFont);
		// 内容的策略
		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
		// 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
		HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

		EasyExcel.write(response.getOutputStream())
				.sheet(exportData.getSheetName())
				.head(exportData.getHeaders())
				.registerWriteHandler(horizontalCellStyleStrategy)
				.registerWriteHandler(new ExcelWidthStyleStrategy())
				.doWrite(exportData.getData());
	}

	/**导出数据，获取输出流
	* @Description
	* @author huxiaolong
	* @Date 2021-06-01 16:28:10
	* @param exportData
	* @return java.io.ByteArrayOutputStream
	*/
	public ByteArrayOutputStream export(ExportDataDTO exportData) throws IOException {
		// 头的策略
		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		// 背景设置为红色
		headWriteCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setColor(IndexedColors.WHITE.getIndex());
		headWriteCellStyle.setWriteFont(headWriteFont);
		// 内容的策略
		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
		// 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
		HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		EasyExcel.write(os)
				.sheet(exportData.getSheetName())
				.head(exportData.getHeaders())
				.registerWriteHandler(horizontalCellStyleStrategy)
				.registerWriteHandler(new ExcelWidthStyleStrategy())
				.doWrite(exportData.getData());

		return os;
	}
}