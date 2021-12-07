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
	public static final String ACTIVITY_COVER_URL = DomainConstant.API + "/mh/activity/%d/cover/";
	/** 活动信息外部数据源url */
	public static final String ACTIVITY_INFO_URL = DomainConstant.API + "/mh/activity/%d/info";
	/** 活动精简信息外部数据源url */
	public static final String ACTIVITY_BRIEF_INFO_URL = DomainConstant.API + "/mh/v3/activity/brief/info";
	/** 活动报名信息外部数据源url */
	public static final String ACTIVITY_SIGN_INFO_URL = DomainConstant.API + "/mh/v2/activity/%d/info";
	/** 推荐活动外部数据源url */
	public static final String ACTIVITY_RECOMMEND_URL = DomainConstant.API + "/mh/activity/%d/recommend";
	/** 双选会外部数据源url */
	public static final String DUAL_SELECT_URL = DomainConstant.DUAL_SELECT + "/portal/api/org/double/selection/statistics?activityId=%d";
	/** 报名签到外部数据源url */
	public static final String ACTIVITY_SIGN_URL = DomainConstant.API + "/mh/activity/%d/sign/btn";
	/** 活动海报的地址 */
	public static final String ACTIVITY_POSTERS_URL = DomainConstant.WEB + "/activity/%d/poster";

}
