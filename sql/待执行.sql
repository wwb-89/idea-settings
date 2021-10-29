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

-- 第二课堂系统模板增加现场报名组件
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (205, 28, 2, 42, '现场报名', '开启后，允许未报名人员扫签到码报名，忽略报名限制条件', 0, 21, 0, NULL, NULL, NULL, NULL);


-- 数据推送
CREATE TABLE `t_activity_data_push_record`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_id` int(0) NULL COMMENT '配置id',
    `activity_id` int(0) NULL COMMENT '活动id',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `target_identify` varchar(50) NULL COMMENT '目标主键标识',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT = '活动市场数据推送记录表';

CREATE TABLE `t_data_push_config`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `name` varchar(50) NULL COMMENT '推送名称',
    `data_type` varchar(50) NULL COMMENT '推送数据类型',
    `way` varchar(50) NULL COMMENT '推送方式',
    `way_value` varchar(255) NULL COMMENT '推送方式值',
    `is_enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人id',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人id',
    PRIMARY KEY (`id`)
) COMMENT = '活动市场数据推送配置表';

CREATE TABLE `t_data_push_form_config`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_id` int(11) NULL COMMENT '配置id',
    `market_id` int(11) NULL COMMENT '活动市场id',
    `form_field_label` varchar(50) NULL COMMENT '表单字段名称',
    `data_field` varchar(50) NULL COMMENT '数据字段',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT = '活动市场数据推送表单配置';

CREATE TABLE `t_user_data_push_record`  (
    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_id` int(0) NULL COMMENT '数据推送配置id',
    `activity_id` int(0) NULL COMMENT '活动id',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `uid` int(0) NULL COMMENT '用户id',
    `target_identify` varchar(50) NULL COMMENT '目标主键标识',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT = '用户数据推送记录表';
ALTER TABLE t_user_stat_summary ADD activity_integral DECIMAL ( 10, 2 ) COMMENT '活动积分';
UPDATE t_user_stat_summary t, t_activity t1 SET t.activity_integral = t1.integral WHERE t.activity_id = t1.id;

