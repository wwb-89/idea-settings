ALTER TABLE t_activity ADD COLUMN is_open_group TINYINT(1) DEFAULT 0 COMMENT '小组id';
ALTER TABLE t_activity ADD COLUMN group_bbsid VARCHAR(50) COMMENT '小组bbsid';

CREATE TABLE `t_activity_flag_code`
(
    `activity_flag` varchar(50) NOT NULL COMMENT '' 活动标识 '',
    `area_code`     varchar(50) NOT NULL COMMENT '' 区域code '',
    PRIMARY KEY (`activity_flag`, `area_code`) USING BTREE,
    UNIQUE KEY `idx_flag` (`activity_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''活动标识区域code关联表'';