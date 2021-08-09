-- 门户模版添加网站id字段
ALTER TABLE t_web_template ADD website_id INT(11) COMMENT '网站id';
RENAME TABLE t_activity_market TO t_market;
CREATE TABLE `t_org_market_data_push`  (
    `fid` int(11) NULL COMMENT '机构id',
    `market_id` int(11) NULL COMMENT '市场id',
    INDEX `idx_primary`(`fid`)
) COMMENT = '机构市场数据推送';
ALTER TABLE t_org_data_repo_config ADD is_specify_market TINYINT(1) DEFAULT 0 COMMENT "是否指定活动市场";

ALTER TABLE t_sign_up_fill_info_type ADD form_id INT(11) COMMENT '表单id';

ALTER TABLE t_activity_create_permission ADD market_id INT(11) COMMENT '活动市场id';
ALTER TABLE t_activity_create_permission ADD wfw_sign_up_scope_type INT(11) COMMENT '微服务报名范围类型';
ALTER TABLE t_activity_create_permission ADD wfw_sign_up_scope text COMMENT '微服务报名范围。以","分割的id列表';
ALTER TABLE t_activity_create_permission ADD contacts_sign_up_scope_type INT(11) COMMENT '通讯录报名范围类型';
ALTER TABLE t_activity_create_permission ADD contacts_sign_up_scope text COMMENT '通讯录报名范围。以","分割的id列表';

DROP TABLE t_activity_classify;
DROP TABLE t_activity_classify_new;
DROP TABLE t_activity_flag_sign_module;
DROP TABLE t_activity_sign_module;

CREATE TABLE `t_activity_menu_config`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `menu` varchar(50) NULL COMMENT '菜单',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`)
) COMMENT = '活动菜单配置表';
