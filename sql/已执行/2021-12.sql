-- 归档字段
ALTER TABLE t_activity ADD is_archived TINYINT ( 1 ) DEFAULT 0 COMMENT '是否归档';
-- 课程id、班级id
ALTER TABLE t_activity ADD is_open_clazz_interaction TINYINT (1) DEFAULT  0 COMMENT '是否开启班级互动';
ALTER TABLE t_activity ADD course_id int(11) COMMENT '课程id';
ALTER TABLE t_activity ADD clazz_id int(11) COMMENT '班级id';
-- 班级互动组件
INSERT INTO `t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (48, 0, '班级互动', 'clazz_interaction', 0, '', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-12-02 15:14:20', 172649568, '2021-12-02 15:14:20', 172649568);
-- 更新teacher模板字段排序，每个排序值增大五倍，以便后插入字段能够排序在其中间
UPDATE t_template_component set `sequence` = `sequence` * 5 where template_id = 4 and `sequence` != -1;
-- teacher 系统模板增加班级互动组件模板组件关联
INSERT INTO `t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (228, 0, 4, 48, '班级互动', '', 0, 46, 0, NULL, NULL, NULL, NULL);