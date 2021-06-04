package com.chaoxing.activity.util.constant;

import java.math.BigDecimal;
import java.time.Duration;
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
	/** groupName分隔符 */
	public static final String GROUP_NAME_SEPARATOR = "/";
	/** 默认的分隔符 */
	public static final String DEFAULT_SEPARATOR = ",";
	/** 登录认证密钥 */
	public static final String LOGIN_AUTH_KEY = "9nfNzEXVknFLcSER";

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
	
	/** 活动开始通知的时间阈值（提前多久发通知）单位：毫秒 */
	public static final Long ACTIVITY_NOTICE_TIME_MILLISECOND = 24 * 60 * 60 * 1000L;
	/** 通知发送人的uid */
	public static final Integer NOTICE_SEND_UID = 168054129;
	/** 超星网fid */
	public static final Integer CX_NETWORK_FID = 0;
	/** 队列获取数据等待时间 */
	public static final Duration QUEUE_GET_WAIT_TIME = Duration.ofMinutes(1);
	/** 最大失败次数 */
	public static final Integer MAX_ERROR_TIMES = 5;
	/** 延时队列时间 */
	public static final Duration DELAYED_QUEUE_DURATION = Duration.ofSeconds(2);

	/** 默认封面云盘id */
	public static final String ACTIVITY_DEFAULT_COVER_CLOUD_ID = "ce257d8a6c546bcedcc7f415dd504296";

	// 表单相关
	/** 表单审批同意 */
	public static final Integer FORM_APPROVAL_AGREE_VALUE = 2;
	/** 表单创建活动选择的门户模版id */
	public static final Integer DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID = 16;

}