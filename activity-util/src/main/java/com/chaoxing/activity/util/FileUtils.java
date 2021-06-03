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

	/** 获取文件的目录
	 * @Description
	 * @author
	 * @Date 2018-04-28 14:36:33
	 * @param filePath
	 * @return java.lang.String
	 */
	public static String getFileDir(String filePath) {
		if (!org.springframework.util.StringUtils.isEmpty(filePath)) {
			filePath = filePath.replaceAll("%20", " ");
		}
		File file = new File(filePath);
		return getFileDir(file);
	}

	/** 获取文件的目录
	 * @Description
	 * @author
	 * @Date 2018-04-28 14:35:42
	 * @param file
	 * @return java.lang.String
	 */
	public static String getFileDir(File file) {
		if (file.isDirectory()) {
			return file.getPath();
		}
		return file.getParent();
	}

	/**文件删除
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-01 16:18:22
	 * @param fileName
	 * @return void
	 */
	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/**创建目录
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-03 15:05:14
	 * @param fileName
	 * @return void
	*/
	public static void createFolder(String fileName) {
		File file = new File(fileName);
		if (file.isDirectory()) {
			file.mkdirs();
		} else {
			file.getParentFile().mkdirs();
		}
	}

}
