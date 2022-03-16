-- 2022-03-01
CREATE TABLE `t_blacklist_detail` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `market_id` int(11) DEFAULT NULL COMMENT '市场id',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `uid` int(11) DEFAULT NULL COMMENT '用户id',
    `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
    `account` varchar(50) DEFAULT NULL COMMENT '账号',
    `breach_content` varchar(255) DEFAULT NULL COMMENT '违约信息',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '违约时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='黑名单违约记录明细';

-- 2022-03-04 系统菜单增加作品征集，刷数据
INSERT INTO t_activity_menu_config ( activity_id, menu, is_enable, show_rule, data_origin )
SELECT
    t.id,
    'works' AS menu,
    1 AS is_enable,
    'no_limit' AS show_rule,
    'system' AS data_origin
FROM
    t_activity t
WHERE
        t.is_open_work = 1 AND t.work_id IS NOT NULL;
-- 2022-03-11
UPDATE t_sign_up_wfw_form_template t
SET t.type = 'wfw_form'
WHERE t.type = 'normal';

-- 2022-03-14
ALTER TABLE t_certificate_issue ADD qr_code_status INT DEFAULT 2 COMMENT '二维码状态';
ALTER TABLE t_certificate_issue ADD qr_code_cloud_id VARCHAR(50) COMMENT '二维码云盘id';
-- 组件表增加是否开关字段判断
ALTER TABLE t_component ADD is_switch_btn TINYINT(1) DEFAULT 0 COMMENT '是否开关组件，0：否，1：是';
-- 模板组件关联表增加开关对应属性值
ALTER TABLE t_template_component ADD is_show TINYINT(1) DEFAULT 1 COMMENT '是否显示，0：否，1：是';
ALTER TABLE t_template_component ADD is_disabled TINYINT(1) DEFAULT 0 COMMENT '是否禁用，0：否，1：是';
ALTER TABLE t_template_component ADD is_open TINYINT(1) DEFAULT 0 COMMENT '是否默认开启，0：否，1：是';
-- 更新component组件中switch组件
update t_component set is_switch_btn = 1 where `code` in ('work', 'activity_rating',
'timing_release', 'reading', 'group', 'inspection_config', 'clazz_interaction',
'push_reminder', 'company_sign_up', 'sign_up', 'sign_up_fill_info', 'sign_up_review',
'sign_up_public_list', 'sign_up_cancel_signed_up', 'sign_up_condition', 'on_site_sign_up');

