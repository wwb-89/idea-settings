-- 2021-06-09
ALTER TABLE `t_activity` ADD COLUMN is_timing_release TINYINT ( 1 ) DEFAULT 0 COMMENT '是否定时发布';
ALTER TABLE `t_activity` ADD COLUMN timing_release_time datetime COMMENT '定时发布时间';

ALTER TABLE `t_activity_stat` ADD COLUMN pv_increment INT(11) DEFAULT 0 COMMENT '浏览量增量';
ALTER TABLE `t_activity_stat` ADD COLUMN signed_up_increment INT(11) DEFAULT 0 COMMENT '报名人数增量';
ALTER TABLE `t_activity_stat` ADD COLUMN signed_in_increment INT(11) DEFAULT 0 COMMENT '签到人数增量';

UPDATE t_activity t
SET t.is_timing_release = 0,
    t.timing_release_time = NULL
WHERE
    ISNULL(t.is_timing_release);