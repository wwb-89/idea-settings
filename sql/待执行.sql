update t_activity_manager set menu= CONCAT(menu, ',notice,setting') where menu is not null and menu != '';

ALTER TABLE t_market ADD is_enable_contacts TINYINT(1) DEFAULT 1 COMMENT '是否启用通讯录';
ALTER TABLE t_market ADD is_enable_organization TINYINT(1) DEFAULT 1 COMMENT '是否启用组织架构';
ALTER TABLE t_market ADD is_enable_regional TINYINT(1) DEFAULT 1 COMMENT '是否启用区域架构';

CREATE TABLE `t_big_data_point_push_record`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `point_type` int(11) NULL COMMENT '积分类型',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `uid`)
) COMMENT = '大数据积分推送记录表';


-- 更新活动管理列表排序值sequence，增大10倍
UPDATE t_table_field_detail SET sequence = (sequence * 10) WHERE table_field_id = 4;
-- 活动管理列表增加可配置展示字段
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (46, 4, '活动分类', 'activityClassify', 1, 1, 0, 1, 0, 120, 'LEFT', 61);
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (47, 4, '人数限制', 'personLimit', 0, 1, 0, 1, 0, 90, 'LEFT', 120);
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (48, 4, '签到数', 'signedInNum', 0, 1, 0, 1, 0, 90, 'LEFT', 130);
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (49, 4, '签到率', 'signedInRate', 0, 1, 0, 1, 0, 90, 'LEFT', 140);
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (50, 4, '评价数', 'rateNum', 0, 1, 0, 1, 0, 90, 'LEFT', 150);
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (51, 4, '活动评分', 'rateScore', 0, 1, 0, 1, 0, 90, 'LEFT', 160);
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (52, 4, '合格人数', 'qualifiedNum', 0, 1, 0, 1, 0, 90, 'LEFT', 170);

-- 成绩考核增加已填报表单采集数量字段
INSERT INTO `activity_engine`.`t_table_field_detail` (`id`, `table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) VALUES (53, 3, '表单采集', 'filledFormCollectionNum', 0, 1, 0, 1, 0, 90, 'LEFT', 12);


-- 新增通用系统组件: 考核管理
INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (45, 0, '考核设置', 'inspection_config', 0, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-10-19 17:29:20', 172649568, '2021-10-19 17:29:20', 172649568);