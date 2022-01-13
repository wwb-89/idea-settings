-- 2022-01-13 积分、学时、学分 且都可以纠正
ALTER TABLE t_user_stat_summary ADD `corrected_integral` DECIMAL(10, 2) DEFAULT NULL COMMENT '校正的积分';
ALTER TABLE t_user_stat_summary ADD `period` DECIMAL(10, 2) DEFAULT 0 COMMENT '学时';
ALTER TABLE t_user_stat_summary ADD `corrected_period` DECIMAL(10, 2) DEFAULT NULL COMMENT '校正的学时';
ALTER TABLE t_user_stat_summary ADD `credit` DECIMAL(10, 2) DEFAULT 0 COMMENT '学分';
ALTER TABLE t_user_stat_summary ADD `corrected_credit` DECIMAL(10, 2) DEFAULT NULL COMMENT '校正的学分';
ALTER TABLE t_user_stat_summary ADD `comment` VARCHAR(512) DEFAULT '' COMMENT '评语';
UPDATE t_user_stat_summary t,
    t_activity t1
SET t.period = t1.period,
    t.credit = t1.credit
WHERE
    t.activity_id = t1.id;


-- 更新图标命名
UPDATE t_icon SET name = '评估', description = '评估' WHERE id = 11;
UPDATE t_icon SET name = '审批', description = '审批' WHERE id = 17;
UPDATE t_icon SET name = '二维码', description = '二维码' WHERE id = 29;
UPDATE t_icon SET name = '考试', description = '考试' WHERE id = 12;
UPDATE t_icon SET name = '表单', code='icon-form' description = '表单' WHERE id = 4;
UPDATE t_icon SET name = '项目', description = '项目' WHERE id = 5;
UPDATE t_icon SET name = '地图2', description = '地图2' WHERE id = 31;
UPDATE t_icon SET default_icon_cloud_id = 'c994db0612ef9f0897f2edb4c2b5f3dd', active_icon_cloud_id = 'a8faf16441ae71e2e928193e4d9ddb53' WHERE id = 26;

-- 插入新的图标
INSERT INTO `activity_engine`.`t_icon` (`id`, `name`, `code`, `default_icon_cloud_id`, `active_icon_cloud_id`, `description`) VALUES (36, '收集', 'icon-collection', '39c9f426d1d1802ba22f32715cc20c8a', '675af20ac3dff94b6c8a6cd075db489f', '收集');

ALTER TABLE t_activity_menu_config ADD `sequence` INT(11) COMMENT '排序字段';

UPDATE t_activity_menu_config SET `sequence` = 1 WHERE `menu` = 'sign_up';
UPDATE t_activity_menu_config SET `sequence` = 10 WHERE `menu` = 'sign_in';
UPDATE t_activity_menu_config SET `sequence` = 20 WHERE `menu` = 'form_collection';
UPDATE t_activity_menu_config SET `sequence` = 30 WHERE `menu` = 'results_manage';
UPDATE t_activity_menu_config SET `sequence` = 40 WHERE `menu` = 'certificate';
UPDATE t_activity_menu_config SET `sequence` = 50 WHERE `menu` = 'notice';
UPDATE t_activity_menu_config SET `sequence` = 60 WHERE `menu` = 'stat';
UPDATE t_activity_menu_config SET `sequence` = 70 WHERE `menu` = 'task';
UPDATE t_activity_menu_config SET `sequence` = 80 WHERE `menu` = 'discuss';
UPDATE t_activity_menu_config SET `sequence` = 90 WHERE `menu` = 'homework';
UPDATE t_activity_menu_config SET `sequence` = 100 WHERE `menu` = 'review_management';
UPDATE t_activity_menu_config SET `sequence` = 500 WHERE `menu` = 'setting';
UPDATE t_activity_menu_config SET `sequence` = 150 WHERE `menu` not in ('sign_up','sign_in','form_collection','results_manage','certificate','notice','stat','task','discuss','homework','review_management','setting');
UPDATE t_activity_menu_config SET is_system = 1 WHERE `menu` in ('sign_up','sign_in','form_collection','results_manage','certificate','notice','stat','task','discuss','homework','review_management','setting');
