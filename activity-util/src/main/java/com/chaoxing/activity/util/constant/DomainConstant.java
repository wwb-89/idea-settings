package com.chaoxing.activity.util.constant;

import com.chaoxing.activity.util.YamlUtils;

/**域名常量
 * @author wwb
 * @version ver 1.0
 * @className DomainConstant
 * @description
 * @blame wwb
 * @date 2021-11-18 14:11:07
 */
public class DomainConstant {

    public DomainConstant() {

    }

    /** 域名资源文件的类路径 */
    private static final String DOMAIN_RESOURCE_CLASS_PATH = "resource.yml";

    /** 主域名 */
    public static final String MAIN = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.main");
    /** api 域名 */
    public static final String API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.api");
    /** web 域名 */
    public static final String WEB = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.web");
    /** 管理端域名 */
    public static final String ADMIN = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.admin");
    /** 报名签到web域名 */
    public static final String SIGN_WEB = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.sign_web");
    /** 报名签到api域名 */
    public static final String SIGN_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.sign_api");
    /** 作品征集的域名 */
    public static final String WORK = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.work");
    /** 作品征集api域名 */
    public static final String WORK_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.work_api");
    /** 门户域名 */
    public static final String MH = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.mh");
    /** 双选会域名 */
    public static final String DUAL_SELECT = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.dual_select");
    /** passport域名 */
    public static final String PASSPORT = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.passport");

    // 微服务
    /** 微服务域名 */
    public static final String WFW = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.wfw");
    /** 微服务组织架构域名 */
    public static final String WFW_ORGANIZATION = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.wfw_organization");
    /** 微服务区域管理域名 */
    public static final String WFW_AREA_MANAGE = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.wfw_area_manage");
    /** 微服务通讯录域名 */
    public static final String WFW_CONTACTS = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.wfw_contacts");
    
    /** 云盘api域名 */
    public static final String CLOUD_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.cloud_api");
    /** 云盘资源域名 */
    public static final String CLOUD_RESOURCE = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.cloud_resource");
    /** 星阅读域名 */
    public static final String START_READ = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.start_read");
    /** 教师发展域名 */
    public static final String TEACHER = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.teacher");
    /** 打卡域名 */
    public static final String PUNCH = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.punch");
    /** 活动发布平台域名 */
    public static final String ACTIVITY = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.activity");
    /** 校园阅读域名 */
    public static final String XUEYA = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.xueya");
    /** 百度地图api域名 */
    public static final String BAIDU_MAP_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.baidu_map_api");
    /** 万能表单域名 */
    public static final String WFW_FORM_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.wfw_form_api");
    /** 厦门教师研修平台域名 */
    public static final String XIAMEN_TRAINING_PLATFORM_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.xiamen_training_platform_api");

    // 小组
    /** 小组api域名 */
    public static final String GROUP_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.group_api");
    /** 小组web域名 */
    public static final String GROUP_WEB = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.group_web");

    // 学习通
    /** 学习通域名 */
    public static final String LEARN = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.learn");
    /** 学习通通知域名 */
    public static final String NOTICE = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.notice");

    //大数据
    /** 大数据积分（新增）域名 */
    public static final String BIGDATA_SCORE_ADD = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.bigdata_score_add");
    /** 大数据积分（消耗）域名 */
    public static final String BIGDATA_SCORE_SPEND = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.bigdata_score_spend");

    // 用户中心
    /** 用户中心域名 */
    public static final String UC = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.uc");

    /** 幕课api域名 */
    public static final String MOOC_API = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.mooc_api");
    /** 幕课域名 */
    public static final String MOOC = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.mooc");

    /** 积分域名 */
    public static final String SCORE = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.score");

    /** 头像服务域名 */
    public static final String PHOTO = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.photo");
    /** 笔记编辑器域名 */
    public static final String NOTE = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.note");
    /** 空间域名 */
    public static final String SPACE = YamlUtils.getStringValue(DOMAIN_RESOURCE_CLASS_PATH, "domain.space");

}