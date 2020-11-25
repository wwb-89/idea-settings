package com.chaoxing.activity.util.constant;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityMhUrlConstant
 * @description
 * @blame wwb
 * @date 2020-11-25 10:16:23
 */
public class ActivityMhUrlConstant {

	private ActivityMhUrlConstant() {

	}

	/** 活动封面外部数据源url */
	public static final String ACTIVITY_COVER_URL = CommonConstant.API_DOMAIN + "/activity/%d/cover/";
	/** 活动信息外部数据源url */
	public static final String ACTIVITY_INFO_URL = CommonConstant.API_DOMAIN + "/activity/%d/info";
	/** 推荐活动外部数据源url */
	public static final String ACTIVITY_RECOMMEND_URL = CommonConstant.API_DOMAIN + "/activity/%d/recommend";
	/** 报名签到外部数据源url */
	public static final String ACTIVITY_SIGN_URL = "http://api.qd.reading.chaoxing.com/activity/%d/btn";

}
