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
	public static final String WORK_ICON_CLOUD_ID = "f4a786680327ec2970f0b62206b4b240";
	/** 星阅读图标云盘id */
	public static final String STAR_ICON_CLOUD_ID = "1e645ff112493e0b1d76a7fcd346332e";
	/** 打卡云盘id */
	public static final String PUNCH_ICON_CLOUD_ID = "5dc825f7cf9935faeaf7f6d7014ed36f";
	/** 听评课图标云盘id */
	public static final String TPK_ICON_CLOUD_ID = "55dcac753a18ee9fc991399bb22482de";
	/** 测评图标云盘id */
	public static final String EVALUATION_ICON_CLOUD_ID = "4b08c5e26dc89e7de9a72172ab179e2b";

	/* http://api.hd.chaoxing.com/activity/module/forward/{moduleType}/{moduleId} */
	/** 模块访问地址 */
	public static final String MODULE_ACCESS_URL = DomainConstant.API_DOMAIN + "/activity/module/forward/%s/%s";

}