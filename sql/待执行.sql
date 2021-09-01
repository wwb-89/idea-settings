-- 三会一课 模板及组件关联
INSERT INTO `activity_engine`.`t_template` (`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (6, '三会一课', NULL, NULL, 1, 'tcol', NULL, '', 6, '2021-08-25 17:14:47', 172649568, '2021-08-25 17:15:47', 172649568);



INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (109, 0, 6, 1, '会议主题', NULL, 1, 1, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (110, 0, 6, 2, '会议时间', NULL, 1, 2, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (111, 0, 6, 3, '封面', NULL, 1, 3, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (112, 0, 6, 4, '主办方', NULL, 0, 4, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (113, 0, 6, 5, '会议形式', NULL, 0, 5, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (114, 0, 6, 6, '会议类型', NULL, 0, 6, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (115, 0, 6, 11, '发布范围', NULL, 1, 7, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (116, 0, 6, 13, '报名', NULL, 0, 8, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (117, 0, 6, 39, '签到', NULL, 0, 9, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (118, 0, 6, 15, '签退', NULL, 0, 10, 1);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (119, 0, 6, 16, '作品征集', '开启后，参与者可以在活动中提交作品，管理员在征集模块中进行审核评分推优等操作', 0, 11, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (120, 0, 6, 17, '标签', NULL, 0, 12, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (121, 0, 6, 19, '定时发布', '开启后，可以按照设置的时间自动发布活动', 0, 13, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (122, 0, 6, 20, '会议议题', NULL, 0, 14, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (123, 116, 6, 30, '报名时间', NULL, 1, 15, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (124, 116, 6, 33, '人数限制', NULL, 0, 16, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (125, 116, 6, 34, '报名填报信息', '开启后，报名者需填写信息', 0, 17, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (126, 116, 6, 35, '报名需要审核', '报名需活动管理员审核', 0, 18, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (127, 116, 6, 36, '报名名单公开', '开启后，所有人都能看到报名人员名单', 0, 19, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (128, 116, 6, 37, '取消报名', '开启后，报名结束后参与者不能取消报名', 0, 20, 0);
INSERT INTO `activity_engine`.`t_template_component` (`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`) VALUES (129, 0, 6, 40, '基本信息', '', 0, -1, 0);

