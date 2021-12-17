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