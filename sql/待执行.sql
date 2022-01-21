-- 2022-01-19
ALTER TABLE t_activity_stat_summary ADD pv INTEGER DEFAULT 0 COMMENT 'pv';

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
-- 模板自定义菜单增加前缀
UPDATE t_activity_menu_config SET menu = CONCAT('template_', menu) WHERE template_component_id IS NOT NULL;


ALTER TABLE t_activity_custom_app_config ADD COLUMN `is_open_blank` tinyint(1) DEFAULT 1 COMMENT '是否新页面打开菜单';


