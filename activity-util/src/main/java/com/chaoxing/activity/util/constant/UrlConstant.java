package com.chaoxing.activity.util.constant;

/**
 * @author wwb
 * @version ver 1.0
 * @className UrlConstant
 * @description
 * @blame wwb
 * @date 2020-12-21 16:48:30
 */
public class UrlConstant {

	private UrlConstant() {

	}

	/** api域名 */
	public static final String API_DOMAIN = "https://api.hd.chaoxing.com";
	public static final String DOMAIN = "https://hd.chaoxing.com";
	/** 登录地址 */
	public static final String LOGIN_URL = "https://passport2.chaoxing.com/login?loginType=4&newversion=true&refer=";
	/** 管理端登录地址 */
	public static final String MANAGE_LOGIN_URL = "http://v1.chaoxing.com/backSchool/toLogin?refer=";
	/** 发通知logo地址 */
	public static final String NOTICE_LOGO_URL = "https://p.ananas.chaoxing.com/star3/origin/b4d171e29d3c60e97c594e4d2b816bbe.png";
	/** 活动管理地址 */
	public static final String ATIVITY_MANAGE_URL = "http://manage.hd.chaoxing.com/activity/%s";
	/** 活动评价的地址 */
	public static final String ACTIVITY_RATING_URL = "https://hd.chaoxing.com/activity/%d/rating";

	/** 双选会 */
	/** 双选会主页url */
	public static final String DUAL_SELECT_INDEX_URL = "http://appcd.chaoxing.com/form-employment/pc/double/election?activityId=%s&wfwfid=%s";

	/** 作品征集主页 */
	public static final String WORK_INDEX_URL = "https://reading.chaoxing.com/zj/activity/forward/%s";

}