ALTER TABLE t_activity_component_value MODIFY COLUMN `value` varchar(512) DEFAULT NULL COMMENT '值';
ALTER TABLE t_activity_component_value ADD COLUMN cloud_ids TEXT DEFAULT NULL COMMENT '云盘id集合';

