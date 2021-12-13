package com.chaoxing.activity.util.constant;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
	/** 连接符号 */
	public static final String  LINK_CHAR = "/";
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
	public static final BigDecimal DEFAULT_LNG = BigDecimal.valueOf(104.07073444090588000);
	/** 默认维度 */
	public static final BigDecimal DEFAULT_LAT = BigDecimal.valueOf(30.57504123492308400);

	/** 通知发送人的uid */
	public static final Integer NOTICE_SEND_UID = 168054129;
	/** 超星网fid */
	public static final Integer CX_NETWORK_FID = 0;
	/** 队列获取数据等待时间 */
	public static final Duration QUEUE_GET_WAIT_TIME = Duration.ofMinutes(1);
	/** 最大失败次数 */
	public static final Integer MAX_ERROR_TIMES = 5;
	/** 延时队列时间（默认2秒） */
	public static final Duration DELAYED_QUEUE_DURATION = Duration.ofSeconds(2);
	/** 队列处理失败后重新加入队列的延时 */
	public static final Duration FAIL_DELAYED_QUEUE_DURATION = Duration.ofMinutes(1);

	/** 默认封面云盘id */
	public static final String ACTIVITY_DEFAULT_COVER_CLOUD_ID = "3b16823d7d5fc677d13c042479e3c6d0";

	// 表单相关
	/** 表单审批同意 */
	public static final Integer FORM_APPROVAL_AGREE_VALUE = 2;
	/** 表单创建活动选择的门户模版id */
	public static final Integer DEFAULT_FROM_FORM_CREATE_ACTIVITY_TEMPLATE_ID = 16;

	// 活动相关
	/** 活动考核配置签到默认分值 */
	public static final BigDecimal DEFAULT_SIGNED_IN_SCORE = BigDecimal.valueOf(5);

	/** 时间格式化yyyy-MM-dd HH:mm:ss*/
	public static final DateTimeFormatter FULL_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	/** 活动时间格式化 */
	public static final DateTimeFormatter NOTICE_ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
	/** 报名时间格式化 */
	public static final DateTimeFormatter NOTICE_SIGN_UP_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
	/** 换行符号 */
	public static final String NEW_LINE_CHAR = "\n";

}