ALTER TABLE t_activity ADD COLUMN is_open_group TINYINT(1) DEFAULT 0 COMMENT '是否启用小组';
ALTER TABLE t_activity ADD COLUMN group_bbsid VARCHAR(50) COMMENT '小组bbsid';
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (44, 0, '讨论小组', 'group', 0, '开启后，参与者可以在讨论组进行交流互动', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-09-17 11:25:45', 25418810, '2021-09-17 11:27:01', 25418810);

ALTER TABLE t_activity_manager ADD COLUMN menu VARCHAR(255) COMMENT '菜单(多个时用,分隔)';

UPDATE t_activity_manager SET menu = 'sign_up,sign_in,results_manage,stat';

CREATE TABLE `t_activity_flag_code`
(
    `activity_flag` varchar(50) NOT NULL COMMENT '' 活动标识 '',
    `area_code`     varchar(50) NOT NULL COMMENT '' 区域code '',
    PRIMARY KEY (`activity_flag`, `area_code`) USING BTREE,
    UNIQUE KEY `idx_flag` (`activity_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''活动标识区域code关联表'';