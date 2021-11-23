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
                                       `fid` int(11) NOT NULL COMMENT '机构id',
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
CREATE TABLE `t_org_user_data_push_record` (
                                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                               `uid` int(11) DEFAULT NULL COMMENT '用户id',
                                               `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
                                               `form_id` int(11) DEFAULT NULL COMMENT '表单id',
                                               `form_user_id` int(11) DEFAULT NULL COMMENT '表单记录id',
                                               `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
                                               `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构用户数据推送记录';
ALTER TABLE t_user_stat_summary ADD sign_in_leave_num INT(11) DEFAULT 0 COMMENT '签到请假次数';
ALTER TABLE t_user_stat_summary ADD not_sign_in_num INT(11) DEFAULT 0 COMMENT '未签到次数';
ALTER TABLE t_user_stat_summary DROP participate_activity_num;
-- 报名签到中的用户表单数据的推送记录迁移到活动引擎中
INSERT INTO t_org_user_data_push_record ( uid, activity_id, form_id, form_user_id, create_time, update_time )
SELECT
    t.uid,
    t1.id,
    t.form_id,
    t.form_user_id,
    t.create_time,
    t.update_time
FROM
    t_user_sign_form_push_record t INNER JOIN t_activity t1 ON t.sign_id = t1.sign_id;



-- 创建模板明细报名条件表
CREATE TABLE `t_template_sign_up_condition` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                `template_component_id` int(11) DEFAULT NULL COMMENT '模板组件id',
                                                `field_name` varchar(50) DEFAULT NULL COMMENT '字段名称',
                                                `condition` varchar(50) DEFAULT NULL COMMENT '条件',
                                                `value` varchar(50) DEFAULT NULL COMMENT '值',
                                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模版的报名条件明细表';

-- 创建活动报名条件明细表
CREATE TABLE `t_activity_sign_up_condition` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
                                                `template_component_id` int(11) DEFAULT NULL COMMENT '模版组件id',
                                                `field_name` varchar(50) DEFAULT NULL COMMENT '字段名称',
                                                `condition` varchar(50) DEFAULT NULL COMMENT '条件',
                                                `value` varchar(50) DEFAULT NULL COMMENT '值',
                                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名条件明细表';


-- 活动管理列表字段配置数字字段居中显示
UPDATE t_table_field_detail  set align = 'center'
WHERE table_field_id = 4 AND
        `code` in ('signedUpNum', 'personLimit','signedInNum','signedInRate','rateNum','rateScore','qualifiedNum');

ALTER TABLE t_sign_up_condition ADD is_config_on_activity TINYINT(1) DEFAULT 0 COMMENT '是否在活动发布时配置';

CREATE TABLE `t_market_notice_template`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `notice_type` varchar(50) NULL COMMENT '通知类型',
    `receiver_description` varchar(50) NULL COMMENT '收件人描述',
    `title` varchar(255) NULL COMMENT '标题',
    `code_title` varchar(255) NULL COMMENT '标题（代码使用）',
    `content` text NULL COMMENT '内容',
    `code_content` text NULL COMMENT '内容（代码使用）',
    `send_time_description` varchar(50) NULL COMMENT '发送时间描述',
    `is_support_time_config` tinyint(1) NULL DEFAULT 1 COMMENT '是否支持时间配置',
    `delay_hour` int(11) NULL COMMENT '延迟小时数',
    `delay_minute` int(11) NULL COMMENT '延迟分钟数',
    `is_enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
    PRIMARY KEY (`id`)
) COMMENT = '市场通知模版表';

CREATE TABLE `t_system_notice_template`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `notice_type` varchar(50) NULL COMMENT '通知类型',
    `receiver_description` varchar(50) NULL COMMENT '收件人描述',
    `title` varchar(255) NULL COMMENT '标题',
    `code_title` varchar(255) NULL COMMENT '标题（代码使用）',
    `content` text NULL COMMENT '内容',
    `code_content` text NULL COMMENT '内容（代码使用）',
    `send_time_description` varchar(50) NULL COMMENT '发送时间描述',
    `is_support_time_config` tinyint(1) NULL DEFAULT 1 COMMENT '是否支持时间配置',
    `delay_hour` int(11) NULL COMMENT '延迟小时数',
    `delay_minute` int(11) NULL COMMENT '延迟分钟数',
    `is_enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
    `sequence` int(11) NULL COMMENT '顺序',
    PRIMARY KEY (`id`)
) COMMENT = '系统通知模版';
-- 2021-11-18
CREATE TABLE `t_market_sign_up_config`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `sign_up_activity_limit` int(11) NULL DEFAULT 0 COMMENT '同时报名的活动上限',
    `sign_up_btn_name` varchar(50) NULL DEFAULT '报名参与' COMMENT '报名按钮名称',
    `sign_up_keyword` varchar(50) NULL DEFAULT '报名' COMMENT '报名关键字',
    PRIMARY KEY (`id`)
) COMMENT = '活动市场报名配置表';
INSERT INTO t_market_sign_up_config ( market_id, sign_up_activity_limit ) SELECT t.id, t.sign_up_activity_limit FROM t_market t;

CREATE TABLE `t_sign_up_wfw_form_template`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NULL COMMENT '名称',
    `code` varchar(50) NULL COMMENT '编码',
    `sign` varchar(255) NULL COMMENT 'sign',
    `key` varchar(255) NULL COMMENT 'key',
    `form_id` int(0) NULL COMMENT '表单id',
    `fid` int(0) NULL COMMENT '表单所属机构id',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否系统表单',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT = '报名万能表单模版表';
