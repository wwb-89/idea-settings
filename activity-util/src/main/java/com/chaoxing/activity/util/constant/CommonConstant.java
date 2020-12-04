package com.chaoxing.activity.util.constant;

/**
 * @author wwb
 * @version ver 1.0
 * @className CommonConstant
 * @description
 * @blame wwb
 * @date 2020-11-10 15:41:17
 */
public class CommonConstant {

	private CommonConstant() {

	}

	/** 默认的文件路径分隔符 */
	public static final String DEFAULT_FILE_SEPARATOR = "/";
	/** 默认的分隔符 */
	public static final String DEFAULT_SEPARATOR = ",";
	/** 分布式锁默认超时时间 */
	public static final Integer DISTRIBUTED_LOCK_TIMEOUT = 20;
	/** 系统fid */
	public static final Integer SYSTEM_FID = -1;

	/** api域名 */
	public static final String API_DOMAIN = "http://api.hd.reading.chaoxing.com";

	/** 默认页码 */
	public static final Integer DEFAULT_PAGE_NUM = 1;
	/** 默认每页条数 */
	public static final Integer DEFAULT_PAGE_SIZE = 10;

}