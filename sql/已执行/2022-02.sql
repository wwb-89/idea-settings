-- 2022-02-11
CREATE TABLE `t_notice_record` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type` varchar(50) DEFAULT NULL COMMENT '通知类型',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `activity_create_fid` int(11) DEFAULT NULL COMMENT '活动创建机构id',
    `activity_flag` varchar(50) DEFAULT NULL COMMENT '活动标识',
    `title` varchar(50) DEFAULT NULL COMMENT '标题',
    `content` text DEFAULT NULL COMMENT '通知内容',
    `time` datetime COMMENT '通知时间',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`activity_create_fid`,`activity_flag`,`type`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';
-- 2022-02-15 自定义应用接口调用
CREATE TABLE `t_custom_app_interface_call` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组件',
    `template_component_id` int(11) DEFAULT NULL COMMENT '自定义应用模版组件id',
    `url` varchar(255) DEFAULT NULL COMMENT '接口地址',
    `is_create_call` tinyint(1) DEFAULT 0 COMMENT '是否创建时调用',
    `is_release_call` tinyint(1) DEFAULT 0 COMMENT '是否发布时调用',
    `is_cancel_release_call` tinyint(1) DEFAULT 0 COMMENT '是否下架时调用',
    `is_start_call` tinyint(1) DEFAULT 0 COMMENT '是否开始时调用',
    `is_end_call` tinyint(1) DEFAULT 0 COMMENT '是否结束时调用',
    `is_delete_call` tinyint(1) DEFAULT 0 COMMENT '是否删除时调用',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`template_component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义应用接口调用';
CREATE TABLE `t_custom_app_interface_call_record` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `template_component_id` int(11) DEFAULT NULL COMMENT '自定义应用模版组件id',
    `call_time` datetime DEFAULT NULL COMMENT '调用时间',
    `status` int(11) DEFAULT NULL COMMENT '状态。0：失败，1：成功',
    `message` varchar(255) DEFAULT NULL COMMENT '错误原因',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`activity_id`,`template_component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义应用接口调用记录表';

ALTER TABLE t_custom_app_interface_call ADD component_id INT ( 11 ) NULL COMMENT '自定义组件id';
-- 修复万能表单创建的活动的origin_type都为normal的问题
UPDATE t_activity t SET t.origin_type = 'wfw_form' WHERE t.origin_type = 'normal' AND t.origin_form_user_id IS NOT NULL;
-- 2022-02-18
CREATE TABLE `t_flag_default_system_menu_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `flag` varchar(50) DEFAULT NULL COMMENT '活动标识',
    `menu` varchar(50) DEFAULT NULL COMMENT '菜单',
    `sequence` int(11) DEFAULT NULL COMMENT '顺序',
    `show_rule` varchar(50) DEFAULT NULL COMMENT '显示规则(no_limit, before_sign_up, after_sign_up)',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_primary` (`flag`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COMMENT='活动标识默认系统菜单配置表';
INSERT INTO `t_flag_default_system_menu_config` VALUES (1, 'teacher', 'sign_up', 1, 'no_limit', 0, '2022-02-18 14:42:02', '2022-02-18 14:59:55');
INSERT INTO `t_flag_default_system_menu_config` VALUES (2, 'teacher', 'sign_in', 10, 'no_limit', 1, '2022-02-18 14:42:03', '2022-02-18 15:00:00');
INSERT INTO `t_flag_default_system_menu_config` VALUES (3, 'teacher', 'form_collection', 20, 'no_limit', 1, '2022-02-18 14:42:04', '2022-02-18 15:00:01');
INSERT INTO `t_flag_default_system_menu_config` VALUES (4, 'teacher', 'results_manage', 30, 'no_limit', 1, '2022-02-18 14:42:04', '2022-02-18 15:00:05');
INSERT INTO `t_flag_default_system_menu_config` VALUES (5, 'teacher', 'certificate', 40, 'no_limit', 0, '2022-02-18 14:42:05', '2022-02-18 15:00:07');
INSERT INTO `t_flag_default_system_menu_config` VALUES (6, 'teacher', 'notice', 50, 'no_limit', 0, '2022-02-18 14:42:06', '2022-02-18 15:00:09');
INSERT INTO `t_flag_default_system_menu_config` VALUES (7, 'teacher', 'stat', 60, 'no_limit', 1, '2022-02-18 14:42:07', '2022-02-18 15:01:48');
INSERT INTO `t_flag_default_system_menu_config` VALUES (8, 'teacher', 'task', 70, 'no_limit', 0, '2022-02-18 14:42:08', '2022-02-18 15:09:58');
INSERT INTO `t_flag_default_system_menu_config` VALUES (9, 'teacher', 'discuss', 80, 'no_limit', 0, '2022-02-18 14:42:09', '2022-02-18 15:10:00');
INSERT INTO `t_flag_default_system_menu_config` VALUES (10, 'teacher', 'homework', 90, 'no_limit', 1, '2022-02-18 14:42:10', '2022-02-18 15:11:59');
INSERT INTO `t_flag_default_system_menu_config` VALUES (11, 'teacher', 'review_management', 100, 'no_limit', 0, '2022-02-18 14:42:10', '2022-02-18 15:10:58');
INSERT INTO `t_flag_default_system_menu_config` VALUES (12, 'teacher', 'to_sign_up', 110, 'before_sign_up', 0, '2022-02-18 14:45:07', '2022-02-18 15:11:00');
INSERT INTO `t_flag_default_system_menu_config` VALUES (13, 'teacher', 'enter_venue', 120, 'no_limit', 0, '2022-02-18 14:45:08', '2022-02-18 15:11:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (14, 'teacher', 'to_sign_in', 130, 'after_sign_up', 0, '2022-02-18 14:45:09', '2022-02-18 15:11:03');
INSERT INTO `t_flag_default_system_menu_config` VALUES (15, 'teacher', 'to_fill_form_collection', 140, 'after_sign_up', 0, '2022-02-18 14:45:10', '2022-02-18 15:11:05');
INSERT INTO `t_flag_default_system_menu_config` VALUES (16, 'teacher', 'to_reading', 150, 'after_sign_up', 0, '2022-02-18 14:45:11', '2022-02-18 15:11:06');
INSERT INTO `t_flag_default_system_menu_config` VALUES (17, 'teacher', 'to_discussion_group', 160, 'after_sign_up', 0, '2022-02-18 14:45:12', '2022-02-18 15:11:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (18, 'teacher', 'to_rate', 170, 'after_sign_up', 0, '2022-02-18 14:45:13', '2022-02-18 15:11:10');
INSERT INTO `t_flag_default_system_menu_config` VALUES (19, 'teacher', 'to_manage', 180, 'no_limit', 0, '2022-02-18 14:45:13', '2022-02-18 15:11:13');
INSERT INTO `t_flag_default_system_menu_config` VALUES (20, 'teacher', 'to_sign_up_info', 190, 'after_sign_up', 0, '2022-02-18 14:45:14', '2022-02-18 15:11:14');
INSERT INTO `t_flag_default_system_menu_config` VALUES (21, 'teacher', 'to_class_interaction_homepage', 200, 'after_sign_up', 0, '2022-02-18 14:45:15', '2022-02-18 15:11:15');
INSERT INTO `t_flag_default_system_menu_config` VALUES (22, 'teacher', 'activity_ended', 210, 'no_limit', 0, '2022-02-18 14:45:16', '2022-02-18 15:11:18');
INSERT INTO `t_flag_default_system_menu_config` VALUES (23, 'training', 'sign_up', 1, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (24, 'training', 'sign_in', 10, 'no_limit', 1, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (25, 'training', 'form_collection', 20, 'no_limit', 1, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (26, 'training', 'results_manage', 30, 'no_limit', 1, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (27, 'training', 'certificate', 40, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (28, 'training', 'notice', 50, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (29, 'training', 'stat', 60, 'no_limit', 1, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (30, 'training', 'task', 70, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (31, 'training', 'discuss', 80, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (32, 'training', 'homework', 90, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:14:19');
INSERT INTO `t_flag_default_system_menu_config` VALUES (33, 'training', 'review_management', 100, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (34, 'training', 'to_sign_up', 110, 'before_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (35, 'training', 'enter_venue', 120, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (36, 'training', 'to_sign_in', 130, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (37, 'training', 'to_fill_form_collection', 140, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (38, 'training', 'to_reading', 150, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (39, 'training', 'to_discussion_group', 160, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (40, 'training', 'to_rate', 170, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (41, 'training', 'to_manage', 180, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (42, 'training', 'to_sign_up_info', 190, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (43, 'training', 'to_class_interaction_homepage', 200, 'after_sign_up', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (44, 'training', 'activity_ended', 210, 'no_limit', 0, '2022-02-18 15:13:56', '2022-02-18 15:13:56');
INSERT INTO `t_flag_default_system_menu_config` VALUES (54, 'subject', 'sign_up', 1, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (55, 'subject', 'sign_in', 10, 'no_limit', 1, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (56, 'subject', 'form_collection', 20, 'no_limit', 1, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (57, 'subject', 'results_manage', 30, 'no_limit', 1, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (58, 'subject', 'certificate', 40, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (59, 'subject', 'notice', 50, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (60, 'subject', 'stat', 60, 'no_limit', 1, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (61, 'subject', 'task', 70, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (62, 'subject', 'discuss', 80, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (63, 'subject', 'homework', 90, 'no_limit', 1, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (64, 'subject', 'review_management', 100, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (65, 'subject', 'to_sign_up', 110, 'before_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (66, 'subject', 'enter_venue', 120, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (67, 'subject', 'to_sign_in', 130, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (68, 'subject', 'to_fill_form_collection', 140, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (69, 'subject', 'to_reading', 150, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (70, 'subject', 'to_discussion_group', 160, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (71, 'subject', 'to_rate', 170, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (72, 'subject', 'to_manage', 180, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (73, 'subject', 'to_sign_up_info', 190, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (74, 'subject', 'to_class_interaction_homepage', 200, 'after_sign_up', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (75, 'subject', 'activity_ended', 210, 'no_limit', 0, '2022-02-18 15:14:02', '2022-02-18 15:14:02');
INSERT INTO `t_flag_default_system_menu_config` VALUES (85, 'studio', 'sign_up', 1, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (86, 'studio', 'sign_in', 10, 'no_limit', 1, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (87, 'studio', 'form_collection', 20, 'no_limit', 1, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (88, 'studio', 'results_manage', 30, 'no_limit', 1, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (89, 'studio', 'certificate', 40, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (90, 'studio', 'notice', 50, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (91, 'studio', 'stat', 60, 'no_limit', 1, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (92, 'studio', 'task', 70, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (93, 'studio', 'discuss', 80, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (94, 'studio', 'homework', 90, 'no_limit', 1, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (95, 'studio', 'review_management', 100, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (96, 'studio', 'to_sign_up', 110, 'before_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (97, 'studio', 'enter_venue', 120, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (98, 'studio', 'to_sign_in', 130, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (99, 'studio', 'to_fill_form_collection', 140, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (100, 'studio', 'to_reading', 150, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (101, 'studio', 'to_discussion_group', 160, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (102, 'studio', 'to_rate', 170, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (103, 'studio', 'to_manage', 180, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (104, 'studio', 'to_sign_up_info', 190, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (105, 'studio', 'to_class_interaction_homepage', 200, 'after_sign_up', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
INSERT INTO `t_flag_default_system_menu_config` VALUES (106, 'studio', 'activity_ended', 210, 'no_limit', 0, '2022-02-18 15:14:08', '2022-02-18 15:14:08');
