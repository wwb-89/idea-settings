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
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb