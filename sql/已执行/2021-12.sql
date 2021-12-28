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
-- 2021-12-07
INSERT INTO `t_system_notice_template` (`id`, `notice_type`, `receiver_description`, `title`, `code_title`, `content`, `code_content`, `send_time_description`, `is_support_time_config`, `delay_hour`, `delay_minute`, `is_enable`, `sequence`) VALUES (5, 'sign_up_about_end', '已收藏', '你收藏的<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动名称\" code=\"activity_name\" style=\"width: 55px;\">即将结束报名！', '你收藏的{activity_name}即将结束报名！', '活动名称：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动名称\" code=\"activity_name\" style=\"width: 55px;\"><div>报名时间：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"报名时间\" code=\"sign_up_time\" style=\"width: 55px;\"></div><div>活动时间：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动时间\" code=\"activity_time\" style=\"width: 55px;\"></div>', '活动名称：{activity_name}\n报名时间：{sign_up_time}\n活动时间：{activity_time}\n', '报名结束前', 1, 24, 0, 1, 5);
INSERT INTO `t_system_notice_template` (`id`, `notice_type`, `receiver_description`, `title`, `code_title`, `content`, `code_content`, `send_time_description`, `is_support_time_config`, `delay_hour`, `delay_minute`, `is_enable`, `sequence`) VALUES (6, 'auto_add_to_blacklist', '黑名单用户', '您已被移入<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动主办方\" code=\"activity_organisers\" style=\"width: 65px;\">黑名单！', '您已被移入{activity_organisers}黑名单！', '您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，将在【自动解除时间】自动解除，【需管理员手动解除黑名单】在此期间内将无法参加任何该单位活动，若有疑问请联系单位管理员。 点击此处联系管理员>><div>未签到/签退活动名称：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动名称\" code=\"activity_name\" style=\"width: 55px;\"></div><div>活动时间：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动名称\" code=\"activity_name\" style=\"width: 55px;\"></div>', '您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，将在【自动解除时间】自动解除，【需管理员手动解除黑名单】在此期间内将无法参加任何该单位活动，若有疑问请联系单位管理员。 点击此处联系管理员>>\n\n未签到/签退活动名称：{activity_name}\n活动时间：{activity_time}\n', '未签到/签退', 0, 0, 0, 1, 6);
INSERT INTO `t_system_notice_template` (`id`, `notice_type`, `receiver_description`, `title`, `code_title`, `content`, `code_content`, `send_time_description`, `is_support_time_config`, `delay_hour`, `delay_minute`, `is_enable`, `sequence`) VALUES (7, 'manual_add_to_blacklist', '黑名单用户', '您已被<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动主办方\" code=\"activity_organisers\" style=\"width: 65px;\">移入黑名单！', '您已被{activity_organisers}移入黑名单！', '您好，您已被<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动主办方\" code=\"activity_organisers\" style=\"width: 65px;\">拉入黑名单，若有疑问请联系单位管理员。 点击此处联系管理员>>', '您好，您已被{activity_organisers}拉入黑名单，若有疑问请联系单位管理员。 点击此处联系管理员>>\n', '未签到/签退', 0, 0, 0, 1, 7);
INSERT INTO `t_system_notice_template` (`id`, `notice_type`, `receiver_description`, `title`, `code_title`, `content`, `code_content`, `send_time_description`, `is_support_time_config`, `delay_hour`, `delay_minute`, `is_enable`, `sequence`) VALUES (8, 'remove_from_blacklist', '黑名单用户', '您已被移出<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动主办方\" code=\"activity_organisers\" style=\"width: 65px;\">黑名单！', '您已被移出{activity_organisers}黑名单！', '您好，您已被<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动主办方\" code=\"activity_organisers\" style=\"width: 65px;\">移出黑名单，可参加该单位的活动。<div>移入黑名单时间：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"黑名单进入时间\" code=\"blacklist_add_time\" style=\"width: 95px;\"></div><div>移出黑名单时间：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"黑名单移出时间\" code=\"blacklist_remove_time\" style=\"width: 95px;\"></div>', '您好，您已被{activity_organisers}移出黑名单，可参加该单位的活动。\n\n移入黑名单时间：{blacklist_add_time}\n移出黑名单时间：{blacklist_remove_time}\n', '未签到/签退', 0, 0, 0, 1, 8);

-- 2021-12-10
-- 增加培训training、课题subject、工作室studio模板
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (13, '培训', NULL, NULL, 1, 'training', NULL, NULL, 13, '2021-12-09 14:29:31', 172649568, '2021-12-09 14:15:50', 172649568);
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (14, '课题', NULL, NULL, 1, 'subject', NULL, NULL, 14, '2021-12-09 14:29:31', 172649568, '2021-12-09 14:15:50', 172649568);
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (15, '工作室', NULL, NULL, 1, 'studio', NULL, NULL, 15, '2021-12-09 14:29:31', 172649568, '2021-12-09 14:15:50', 172649568);


-- 增加培训training模板组件关联
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (229, 0, 13, 1, '名称', NULL, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (230, 0, 13, 2, '活动时间', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (231, 0, 13, 3, '封面', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (232, 0, 13, 4, '主办方', NULL, 0, 4, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (233, 0, 13, 5, '类型', NULL, 0, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (234, 0, 13, 6, '分类', NULL, 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (235, 0, 13, 11, '发布范围', '按照区域架构选择活动范围', 1, 9, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (236, 0, 13, 13, '报名', NULL, 0, 10, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (237, 0, 13, 39, '签到', NULL, 0, 11, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (238, 0, 13, 15, '签退', NULL, 0, 12, 1, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (239, 0, 13, 17, '标签', NULL, 0, 13, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (240, 0, 13, 19, '定时发布', '开启后，可以按照设置的时间自动发布活动', 0, 14, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (241, 0, 13, 20, '简介', NULL, 0, 15, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (242, 236, 13, 30, '报名时间', NULL, 1, 16, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (243, 236, 13, 33, '人数限制', NULL, 0, 17, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (244, 236, 13, 34, '报名填报信息', '开启后，报名者需填写信息', 0, 18, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (245, 236, 13, 35, '报名需要审核', '报名需活动管理员审核', 0, 19, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (246, 236, 13, 36, '报名名单公开', '开启后，所有人都能看到报名人员名单', 0, 20, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (247, 236, 13, 37, '取消报名设置', '开启后，指定时间后参与者不能取消报名', 0, 21, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (248, 0, 13, 40, '基本信息', '', 0, -1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (249, 0, 13, 48, '班级互动', '', 0, 46, 0, NULL, NULL, NULL, NULL);
-- 增加课题subject模板组件关联
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (250, 0, 14, 1, '名称', NULL, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (251, 0, 14, 2, '活动时间', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (252, 0, 14, 3, '封面', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (253, 0, 14, 4, '主办方', NULL, 0, 4, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (254, 0, 14, 5, '类型', NULL, 0, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (255, 0, 14, 6, '分类', NULL, 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (256, 0, 14, 11, '发布范围', '按照区域架构选择活动范围', 1, 9, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (257, 0, 14, 13, '报名', NULL, 0, 10, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (258, 0, 14, 39, '签到', NULL, 0, 11, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (259, 0, 14, 15, '签退', NULL, 0, 12, 1, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (260, 0, 14, 17, '标签', NULL, 0, 13, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (261, 0, 14, 19, '定时发布', '开启后，可以按照设置的时间自动发布活动', 0, 14, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (262, 0, 14, 20, '简介', NULL, 0, 15, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (263, 257, 14, 30, '报名时间', NULL, 1, 16, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (264, 257, 14, 33, '人数限制', NULL, 0, 17, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (265, 257, 14, 34, '报名填报信息', '开启后，报名者需填写信息', 0, 18, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (266, 257, 14, 35, '报名需要审核', '报名需活动管理员审核', 0, 19, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (267, 257, 14, 36, '报名名单公开', '开启后，所有人都能看到报名人员名单', 0, 20, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (268, 257, 14, 37, '取消报名设置', '开启后，指定时间后参与者不能取消报名', 0, 21, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (269, 0, 14, 40, '基本信息', '', 0, -1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (270, 0, 14, 48, '班级互动', '', 0, 46, 0, NULL, NULL, NULL, NULL);

-- 增加工作室studio模板组件关联
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (271, 0, 15, 1, '名称', NULL, 1, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (272, 0, 15, 2, '活动时间', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (273, 0, 15, 3, '封面', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (274, 0, 15, 4, '主办方', NULL, 0, 4, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (275, 0, 15, 5, '类型', NULL, 0, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (276, 0, 15, 6, '分类', NULL, 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (277, 0, 15, 11, '发布范围', '按照区域架构选择活动范围', 1, 9, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (278, 0, 15, 13, '报名', NULL, 0, 10, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (279, 0, 15, 39, '签到', NULL, 0, 11, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (280, 0, 15, 15, '签退', NULL, 0, 12, 1, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (281, 0, 15, 17, '标签', NULL, 0, 13, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (282, 0, 15, 19, '定时发布', '开启后，可以按照设置的时间自动发布活动', 0, 14, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (283, 0, 15, 20, '简介', NULL, 0, 15, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (284, 278, 15, 30, '报名时间', NULL, 1, 16, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (285, 278, 15, 33, '人数限制', NULL, 0, 17, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (286, 278, 15, 34, '报名填报信息', '开启后，报名者需填写信息', 0, 18, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (287, 278, 15, 35, '报名需要审核', '报名需活动管理员审核', 0, 19, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (288, 278, 15, 36, '报名名单公开', '开启后，所有人都能看到报名人员名单', 0, 20, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (289, 278, 15, 37, '取消报名设置', '开启后，指定时间后参与者不能取消报名', 0, 21, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (290, 0, 15, 40, '基本信息', '', 0, -1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (291, 0, 15, 48, '班级互动', '', 0, 46, 0, NULL, NULL, NULL, NULL);

-- 2021-12-10
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

-- 进入黑名单系统通知模板文案调整
UPDATE t_system_notice_template
SET `content` = '您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，在此期间内将无法参加任何该单位活动。<div>未签到/签退活动名称：<input type="text" class="tag" readonly="readonly" value="活动名称" code="activity_name" style="width: 55px;"></div><div>活动时间：<input type="text" class="tag" readonly="readonly" value="活动名称" code="activity_name" style="width: 55px;"></div>',
    `code_content` = '您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，

未签到/签退活动名称：{activity_name}
活动时间：{activity_time}
'
WHERE
        id = 6
  AND notice_type = 'auto_add_to_blacklist';
UPDATE t_system_notice_template
SET `content` = '您好，您已被<input type="text" class="tag" readonly="readonly" value="活动主办方" code="activity_organisers" style="width: 65px;">拉入黑名单。',
    `code_content` = '您好，您已被{activity_organisers}拉入黑名单。'
WHERE
        id = 7
  AND notice_type = 'manual_add_to_blacklist';

-- 2021-12-17
-- 2021-12-15
ALTER TABLE t_activity ADD certificate_template_id INT ( 11 ) COMMENT '证书模版id';
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (49, 0, '证书设置', 'certificate', 0, '可配置一个证书，可以给参与者发放证书', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-12-15 15:52:49', 25418810, '2021-12-15 15:54:04', 25418810);
-- 给系统模版添加这个组件
UPDATE t_template_component t SET t.sequence = t.sequence * 10;
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (292, 0, 1, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (293, 0, 2, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (294, 0, 3, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (295, 0, 4, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (296, 0, 5, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (297, 0, 6, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (298, 0, 7, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (299, 0, 8, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (300, 0, 9, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (301, 0, 10, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (302, 0, 11, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (303, 0, 12, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (304, 0, 13, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (305, 0, 14, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (306, 0, 15, 49, '证书设置', '可配置一个证书，可以给参与者发放证书', 0, 461, 0, NULL, NULL, NULL, NULL);
ALTER TABLE t_inspection_config ADD is_auto_issue_certificate TINYINT(1) DEFAULT 0 COMMENT '是否自动发放证书';
CREATE TABLE `t_certificate_issue` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `uid` int(11) DEFAULT NULL COMMENT '发放用户id',
    `no` varchar(50) DEFAULT NULL COMMENT '证书编号',
    `serial_no` int(11) DEFAULT NULL COMMENT '序号',
    `issue_time` datetime DEFAULT NULL COMMENT '发放时间',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`activity_id`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='证书发放表';

ALTER TABLE t_user_result ADD real_name VARCHAR(50) COMMENT '姓名';
ALTER TABLE t_user_result ADD uname VARCHAR(50) COMMENT '用户名';
UPDATE t_user_result t,
    t_user t1
SET t.real_name = t1.real_name,
    t.uname = t1.login_name
WHERE
    t.uid = t1.uid;


-- 报名成功通知模板
INSERT INTO `activity_engine`.`t_system_notice_template` (`id`, `notice_type`, `receiver_description`, `title`, `code_title`, `content`, `code_content`, `send_time_description`, `is_support_time_config`, `delay_hour`, `delay_minute`, `is_enable`, `sequence`) VALUES (9, 'sign_up_success', '报名用户', '成功报名活动<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动名称\" code=\"activity_name\" style=\"width: 55px;\">', '成功报名活动{activity_name}', '您好，您已成功报名活动！<div>活动名称：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动名称\" code=\"activity_name\" style=\"width: 55px;\"></div><div>活动地点：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动地点\" code=\"activity_address\" style=\"width: 55px;\"></div><div>活动地点：<input type=\"text\" class=\"tag\" readonly=\"readonly\" value=\"活动时间\" code=\"activity_time\" style=\"width: 55px;\"></div>', '您好，您已成功报名活动！\n\n活动名称：{activity_name}\n活动地点：{activity_address}\n活动时间：{activity_time}\n', '', 0, 0, 0, 1, 9);

-- 系统通知模板增加is_deleted字段，更新报名即将开始和报名即将结束为删除状态
ALTER TABLE t_system_notice_template ADD is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除';
UPDATE t_system_notice_template SET is_deleted = 1 WHERE id IN (4, 5);

-- 2021-12-20
INSERT INTO `activity_engine`.`t_table_field`(`id`, `type`, `associated_type`, `is_deleted`, `create_time`, `update_time`) VALUES (5, 'certificate_issue', 'activity', 0, '2021-12-20 10:45:43', '2021-12-20 10:45:48');
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (54, 5, '签到次数', 'signedInNum', 0, 1, 0, 0, 1, 50, 'left', 10, 0, '2021-12-20 10:46:48', '2021-12-20 10:50:26');
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (55, 5, '签到率', 'signedInRate', 0, 1, 0, 0, 1, 50, 'left', 20, 0, '2021-12-20 10:46:50', '2021-12-20 10:50:27');
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (56, 5, '参与时长', 'participateTimeLength', 0, 1, 0, 0, 1, 50, 'left', 30, 0, '2021-12-20 10:46:52', '2021-12-20 10:50:28');
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (57, 5, '合格状态', 'qualifiedStatus', 0, 1, 0, 0, 0, 50, 'left', 40, 0, '2021-12-20 10:46:53', '2021-12-20 10:50:30');
-- 2021-12-21
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (58, 3, '参与时长(分钟)', 'participateTimeLength', 0, 1, 0, 1, 1, 120, 'left', 30, 0, '2021-12-21 15:33:19', '2021-12-21 15:36:43');
-- 2021-12-22
-- 增加活动推送提醒组件
INSERT INTO `t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (50, 0, '推送提醒', 'push_reminder', 0, '开启后，可为固定人群发送活动推送', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-12-21 15:14:20', 172649568, '2021-12-21 15:14:20', 172649568);
-- 活动增加是否开启推送提醒字段
ALTER TABLE t_activity ADD is_open_push_reminder TINYINT(1) DEFAULT 0 COMMENT '是否开启推送提醒';
-- 创建活动提醒推送表
CREATE TABLE `t_activity_push_reminder` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `receive_scope` text COMMENT '推送提醒接收范围',
    `content` varchar(255) DEFAULT NULL COMMENT '通知内容',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2021-12-22 审批表单
ALTER TABLE t_sign_up_wfw_form_template ADD type VARCHAR(50) DEFAULT 'normal' COMMENT '类型';
ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_approval_template_id INT(11) COMMENT '万能表单审批模版id';
-- 所有报名填报信息的审批表单模版都使用通用的
UPDATE t_sign_up_fill_info_type t SET t.wfw_form_approval_template_id = 7 WHERE ISNULL(t.wfw_form_approval_template_id);
INSERT INTO `activity_engine`.`t_table_field_detail`(`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`, `is_deleted`, `create_time`, `update_time`) VALUES (59, 4, '标签', 'tags', 0, 1, 0, 0, 0, 120, 'left', 180, 0, '2021-12-27 15:59:12', '2021-12-27 15:59:25');

