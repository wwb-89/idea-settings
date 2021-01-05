package com.chaoxing.activity.util.constant;

import java.math.BigDecimal;
import java.time.ZoneOffset;

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

	/** 默认页码 */
	public static final Integer DEFAULT_PAGE_NUM = 1;
	/** 默认每页条数 */
	public static final Integer DEFAULT_PAGE_SIZE = 10;
	/** 默认时区 */
	public static final ZoneOffset DEFAULT_ZONEOFFSET = ZoneOffset.of("+8");

	/** 默认经度 */
	public static final BigDecimal DEFAULT_LONGITUDE = BigDecimal.valueOf(104.07073444090588000);
	/** 默认维度 */
	public static final BigDecimal DEFAULT_DIMENSION = BigDecimal.valueOf(30.57504123492308400);

}