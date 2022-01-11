ALTER TABLE t_activity_menu_config ADD `is_enable` TINYINT(1) DEFAULT 1 COMMENT '是否启用';
ALTER TABLE t_activity_menu_config ADD `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统菜单';
ALTER TABLE t_activity_menu_config ADD `template_component_id` INT(11) COMMENT '模板组件id';
