-- 2021-06-29
DROP TABLE t_activity_form_record;
CREATE TABLE `t_award`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `name` varchar(50) NULL COMMENT '奖项名称',
    `level` varchar(50) NULL COMMENT '奖项级别',
    `description` varchar(255) NULL COMMENT '说明',
    `is_released` tinyint(1) NULL DEFAULT 0 COMMENT '是否发布',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`)
) COMMENT = '活动奖项表';
CREATE TABLE `t_user_award`  (
    `id` integer(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `award_id` int(11) NULL COMMENT '奖项id',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`award_id`, `uid`),
    INDEX `idx_activity_uid`(`activity_id`, `uid`),
    INDEX `idx_uid`(`uid`)
) COMMENT = '用户获奖表';
CREATE TABLE `t_performance`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uid` int(11) NULL COMMENT '用户id',
    `activity_id` int(11) NULL COMMENT '活动id',
    `score` decimal(10, 2) NULL COMMENT '得分',
    `remark` varchar(255) NULL COMMENT '评语',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `uid`)
) COMMENT = '用户活动表现';
CREATE TABLE `t_market_table_field`  (
    `fid` int(11) NULL COMMENT '机构id',
    `activity_flag` varchar(50) NULL COMMENT '活动标识',
    `table_field_id` int(11) NULL COMMENT '表格字段配置id',
    `table_field_detail_id` int(11) NULL COMMENT '表格字段详细配置id',
    `is_top` tinyint(1) NULL COMMENT '是否置顶',
    `sequence` int(11) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    INDEX `idx_primary`(`fid`, `activity_flag`, `table_field_id`)
) COMMENT = '活动市场表格字段关联表';
CREATE TABLE `t_web_template_activity_flag`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_flag` varchar(50) NULL COMMENT '活动标识',
    `web_template_id` int(11) NULL COMMENT '模版id',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT = '活动标识与门户模版之间的关系';



-- 2021-06-30
-- 成绩考核字段
INSERT INTO `activity_engine`.`t_table_field`(type`, `associated_type`) VALUES ('activity_inspection_manage', 'activity');

INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '姓名', 'realName', 1, 0, 1, 1, 0, 120, 'center', 1 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '签到数', 'signedInNum', 1, 1, 0, 1, 1, 120, 'left', 2 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '签到率', 'signedInRate', 1, 1, 0, 1, 1, 120, 'left', 3 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '评价数', 'ratingNum', 1, 1, 0, 1, 1, 120, 'left', 4 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '积分', 'totalScore', 1, 1, 0, 1, 1, 120, 'left', 5 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '奖项', 'prize', 0, 1, 0, 1, 1, 120, 'left', 6 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '状态', 'qualifiedStatus', 1, 1, 0, 1, 1, 120, 'left', 7 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_inspection_manage' and a.associated_type = 'activity';

-- 活动管理字段

INSERT INTO `activity_engine`.`t_table_field`(`type`, `associated_type`) VALUES ('activity_manage_list', 'activity_market');

INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '封面', 'cover', 1, 1, 1, 1, 0, 120, 'center', 1 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '活动名称', 'name', 1, 1, 0, 1, 0, 120, 'left', 2 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '活动时间', 'activityStartEndTime', 1, 1, 0, 1, 0, 180, 'left', 3 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '创建人', 'createUserName', 1, 1, 0, 1, 0, 90, 'left', 4 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '创建单位', 'createOrgName', 1, 1, 0, 1, 0, 120, 'left', 5 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '报名人数', 'signedUpNum', 1, 1, 0, 1, 0, 90, 'left', 6 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '状态', 'status', 1, 1, 0, 1, 0, 90, 'left', 7 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '海报', 'poster', 1, 1, 0, 1, 0, 120, 'left', 8 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
INSERT INTO `activity_engine`.`t_table_field_detail`(`table_field_id`, `name`, `code`, `is_default_checked`, `is_allow_uncheck`, `is_default_top`, `is_allow_top`, `is_sortable`, `min_width`, `align`, `sequence`) SELECT a.id, '双选会', 'dualSelect', 1, 1, 0, 1, 0, 150, 'left', 9 FROM `activity_engine`.`t_table_field` a WHERE a.type = 'activity_manage_list' and a.associated_type = 'activity_market' ;
