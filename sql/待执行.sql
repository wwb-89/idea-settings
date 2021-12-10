-- 2021-12-10
ALTER TABLE t_data_push_form_config ADD form_field_alias VARCHAR(50) COMMENT '表单字段别名';
ALTER TABLE t_data_push_form_config ADD is_custom_field TINYINT(1) DEFAULT 0 COMMENT '是否是自定义字段';
-- 更新新模板字段排序，每个排序值增大五倍，以便后插入字段能够排序在其中间
UPDATE t_template_component set `sequence` = `sequence` * 5 where template_id in (13, 14, 15) and `sequence` != -1;
UPDATE t_template_component set `sequence` = 46 where template_id in (13, 14, 15) and component_id = 48;


-- 补充系统模板遗漏的报名信息填报模板关联数据
INSERT INTO `activity_engine`.`t_sign_up_fill_info_type` (`id`, `template_component_id`, `type`, `wfw_form_template_id`) VALUES (12, 236, 'wfw_form', 5);
INSERT INTO `activity_engine`.`t_sign_up_fill_info_type` (`id`, `template_component_id`, `type`, `wfw_form_template_id`) VALUES (13, 257, 'wfw_form', 5);
INSERT INTO `activity_engine`.`t_sign_up_fill_info_type` (`id`, `template_component_id`, `type`, `wfw_form_template_id`) VALUES (14, 278, 'wfw_form', 5);