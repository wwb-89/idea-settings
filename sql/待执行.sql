-- 2021-12-21
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (58, 3, '参与时长(分钟)', 'participateTimeLength', 0, 1, 0, 1, 1, 120, 'left', 30, 0, '2021-12-21 15:33:19', '2021-12-21 15:36:43');

-- 审批表单
ALTER TABLE t_sign_up_wfw_form_template ADD type VARCHAR(50) DEFAULT 'normal' COMMENT '类型';
