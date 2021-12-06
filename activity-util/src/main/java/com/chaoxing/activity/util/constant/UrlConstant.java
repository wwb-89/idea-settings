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

	/** 登录地址 */
	public static final String LOGIN_URL = DomainConstant.PASSPORT + "/login?loginType=4&newversion=true&refer=";
	/** 管理端登录地址 */
	public static final String MANAGE_LOGIN_URL = DomainConstant.WFW + "/backSchool/toLogin?refer=";
	/** 发通知logo地址 */
	public static final String NOTICE_LOGO_URL = DomainConstant.CLOUD_RESOURCE + "/star3/origin/b4d171e29d3c60e97c594e4d2b816bbe.png";
	/** 活动管理地址 */
	public static final String ATIVITY_MANAGE_URL = DomainConstant.ADMIN + "/activity/%s";
	/** 活动评价的地址 */
	public static final String ACTIVITY_RATING_URL = DomainConstant.WEB + "/activity/%d/rating";
	/** 门户报名ajax接口地址 */
	public static final String MH_AJAX_SIGN_UP = DomainConstant.API + "/mh/v3/sign-up";


	/** 双选会 */
	/** 双选会主页url */
	public static final String DUAL_SELECT_INDEX_URL = DomainConstant.DUAL_SELECT + "/form-employment/pc/double/election?activityId=%s&wfwfid=%s";

	/** 作品征集主页 */
	public static final String WORK_INDEX_URL = DomainConstant.WORK + "/zj/activity/forward/%s";

	public static String getWorkManageUrl(Integer workId) {
		return DomainConstant.WORK + "/zj/manage/activity/" + workId + "/new?isHideHeader=false";
	}

	public static String getPosterUrl(Integer activityId) {
		return DomainConstant.WEB + "/activity/" + activityId + "/poster";
	}

	public static String getGroupUrl(String bbsid) {
		return DomainConstant.API + "/redirect/group/" + bbsid;
	}

}