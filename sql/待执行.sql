-- 2022-02-11
CREATE TABLE `t_notice_record` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type` varchar(50) DEFAULT NULL COMMENT '通知类型',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `activity_create_fid` int(11) DEFAULT NULL COMMENT '活动创建机构id',
    `activity_flag` varchar(50) DEFAULT NULL COMMENT '活动标识',
    `content` text DEFAULT NULL COMMENT '通知内容',
    `time` datetime COMMENT '通知时间',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`activity_create_fid`,`activity_flag`,`type`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';