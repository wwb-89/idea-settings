ALTER TABLE t_activity_menu_config ADD `is_enable` TINYINT(1) DEFAULT 1 COMMENT '是否启用';
ALTER TABLE t_activity_menu_config ADD `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统菜单';
ALTER TABLE t_activity_menu_config ADD `template_component_id` INT(11) COMMENT '模板组件id';

INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (51, 0, '表单采集', 'form_collection', 0, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-12 15:34:05', 172649568, '2022-01-12 15:34:05', 172649568);