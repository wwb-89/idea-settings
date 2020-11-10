package com.chaoxing.activity.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author wwb
 * @version ver 1.0
 * @className FileUtils
 * @description
 * @blame wwb
 * @date 2020-11-10 15:43:35
 */
public class FileUtils {

	private FileUtils() {

	}

	/**获取文件目录
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 15:44:38
	 * @param file
	 * @return java.lang.String
	*/
	public static String getFileDirectory(File file) {
		if (file.isDirectory()) {
			return file.getPath();
		}
		return file.getParent();
	}

	public static String getFileDirectory(String filePath) {
		if (StringUtils.isNotEmpty(filePath)) {
			filePath = filePath.replaceAll("%20", " ");
		}
		File file = new File(filePath);
		return getFileDirectory(file);
	}

}
