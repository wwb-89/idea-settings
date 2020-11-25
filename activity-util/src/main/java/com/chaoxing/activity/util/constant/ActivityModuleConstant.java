package com.chaoxing.activity.util.constant;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleConstant
 * @description
 * @blame wwb
 * @date 2020-11-25 00:10:28
 */
public class ActivityModuleConstant {

	private ActivityModuleConstant() {

	}

	/** 作品征集图标云盘id */
	public static final String WORK_ICON_CLOUD_ID = "";
	/** 星阅读图标云盘id */
	public static final String STAR_ICON_CLOUD_ID = "";
	/** 打卡云盘id */
	public static final String PUNCH_ICON_CLOUD_ID = "";
	/** 听评课图标云盘id */
	public static final String TPK_ICON_CLOUD_ID = "";

	/** 模块访问地址 */
	/* http://api.new.reading.chaoxing.com/activity/module/forward/{moduleType}/{moduleId} */
	public static final String MODULE_ACCESS_URL = CommonConstant.API_DOMAIN + "/activity/module/forward/%s/%s";

}