ALTER TABLE t_activity ADD COLUMN is_open_group TINYINT(1) DEFAULT 0 COMMENT '小组id';
ALTER TABLE t_activity ADD COLUMN group_bbsid VARCHAR(50) COMMENT '小组bbsid';