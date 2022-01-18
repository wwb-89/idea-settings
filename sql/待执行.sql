UPDATE t_user_stat_summary t,
    t_activity t1
SET t.period = t1.period,
    t.credit = t1.credit
WHERE
    t.activity_id = t1.id;

-- 2022-01-18
INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (52, 13, '角色范围', 'sign_up_role_limit', 0, '开启后，报名者需要是对应角色才能报名', 1, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-18 10:48:54', 25418810, '2022-01-18 10:50:35', 25418810);


-- 创建活动自定义菜单配置表
CREATE TABLE `t_activity_custom_app_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `title` varchar(50) DEFAULT NULL COMMENT '菜单名称',
    `url` varchar(512) DEFAULT NULL COMMENT '菜单url',
    `icon_id` int(11) DEFAULT NULL COMMENT '图标id',
    `type` varchar(50) DEFAULT NULL COMMENT '链接类型，frontend：前端，backend：后端',
    `is_pc` tinyint(1) DEFAULT '1' COMMENT '是否pc端菜单',
    `show_rule` varchar(50) DEFAULT 'no_limit' COMMENT '显示规则(no_limit, before_sign_up, after_sign_up)',
    `is_mobile` tinyint(255) DEFAULT '0' COMMENT '是否移动端菜单',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动自定义菜单配置表';

-- 增加管理员表menu字段长度
ALTER TABLE t_activity_manager MODIFY COLUMN `menu` varchar(1024) DEFAULT NULL COMMENT '菜单(多个时用,分隔)';

-- 删除模板自定义配置表sequence字段
ALTER TABLE t_custom_app_config DROP COLUMN `sequence`;

-- 删除活动菜单配置表中的is_system字段，增加data_origin字段
ALTER TABLE t_activity_menu_config DROP COLUMN `is_system`;
ALTER TABLE t_activity_menu_config ADD COLUMN `show_rule` varchar(50) DEFAULT 'no_limit' COMMENT '显示规则(no_limit, before_sign_up, after_sign_up)';
ALTER TABLE t_activity_menu_config ADD COLUMN `data_origin` varchar(50) DEFAULT NULL COMMENT '菜单来源：system-系统，template-模板，activity-活动';
-- 刷数据
UPDATE t_activity_menu_config SET data_origin = 'system' WHERE template_component_id IS NULL;
UPDATE t_activity_menu_config SET data_origin = 'template' WHERE template_component_id IS NOT NULL;

