-- 归档字段
ALTER TABLE t_activity ADD is_archived TINYINT ( 1 ) DEFAULT 0 COMMENT '是否归档';
