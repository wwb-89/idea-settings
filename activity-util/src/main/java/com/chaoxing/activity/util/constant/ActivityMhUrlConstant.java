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
	public static final String ACTIVITY_COVER_URL = UrlConstant.API_DOMAIN + "/mh/activity/%d/cover/";
	/** 活动信息外部数据源url */
	public static final String ACTIVITY_INFO_URL = UrlConstant.API_DOMAIN + "/mh/activity/%d/info";
	/** 活动报名信息外部数据源url */
	public static final String ACTIVITY_SIGN_INFO_URL = UrlConstant.API_DOMAIN + "/mh/v2/activity/%d/info";
	/** 推荐活动外部数据源url */
	public static final String ACTIVITY_RECOMMEND_URL = UrlConstant.API_DOMAIN + "/mh/activity/%d/recommend";
	/** 双选会外部数据源url */
	public static final String DUAL_SELECT_URL = "http://appcd.chaoxing.com/form-employment/portal/api/org/double/selection/statistics?activityId=%d";
	/** 报名签到外部数据源url */
	public static final String ACTIVITY_SIGN_URL = UrlConstant.API_DOMAIN + "/mh/activity/%d/sign/btn";
	/** 门户域名 */
	public static final String MH_DOMAIN = "http://mh.chaoxing.com";
	/** 活动访问地址 */
	public static final String ACTIVITY_ACCESS_URL = MH_DOMAIN + "/page/%s/show";
	/** 活动海报的地址 */
	public static final String ACTIVITY_POSTERS_URL = UrlConstant.DOMAIN + "/activity/%d/poster";

}
