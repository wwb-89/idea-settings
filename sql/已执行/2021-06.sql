-- 2021-06-09
ALTER TABLE `t_activity` ADD COLUMN is_timing_release TINYINT ( 1 ) DEFAULT 0 COMMENT '是否定时发布';
ALTER TABLE `t_activity` ADD COLUMN timing_release_time datetime COMMENT '定时发布时间';

ALTER TABLE `t_activity_stat` ADD COLUMN pv_increment INT(11) DEFAULT 0 COMMENT '浏览量增量';
ALTER TABLE `t_activity_stat` ADD COLUMN signed_up_increment INT(11) DEFAULT 0 COMMENT '报名人数增量';
ALTER TABLE `t_activity_stat` ADD COLUMN signed_in_increment INT(11) DEFAULT 0 COMMENT '签到人数增量';

UPDATE t_activity t
SET t.is_timing_release = 0,
    t.timing_release_time = NULL
WHERE
    ISNULL(t.is_timing_release);

ALTER TABLE `t_activity` ADD COLUMN time_length_upper_limit INT(11) COMMENT '参与时长上限';
INSERT INTO `activity_engine`.`t_activity_flag_sign_module`(`activity_flag`, `module_type`, `module_name`, `is_enable_limit_participate_scope`, `limit_participate_scope_type`, `custom_sign_up_type`, `btn_name`, `sequence`, `create_time`) VALUES ('volunteer', 'sign_up', '报名', 0, NULL, NULL, NULL, 1, '2021-06-11 09:59:16');
INSERT INTO `activity_engine`.`t_activity_flag_sign_module`(`activity_flag`, `module_type`, `module_name`, `is_enable_limit_participate_scope`, `limit_participate_scope_type`, `custom_sign_up_type`, `btn_name`, `sequence`, `create_time`) VALUES ('volunteer', 'sign_in', '签到', 0, NULL, NULL, NULL, 1, '2021-06-11 09:59:18');
INSERT INTO `activity_engine`.`t_activity_flag_sign_module`(`activity_flag`, `module_type`, `module_name`, `is_enable_limit_participate_scope`, `limit_participate_scope_type`, `custom_sign_up_type`, `btn_name`, `sequence`, `create_time`) VALUES ('volunteer', 'sign_out', '签退', 0, NULL, NULL, NULL, 2, '2021-06-11 09:59:20');