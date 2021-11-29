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

ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_template_id INT(11) COMMENT '报名万能表单模板id';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 1 WHERE type = 'wfw_form' AND template_type = 'normal';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 2 WHERE type = 'wfw_form' AND template_type = 'dual_select_company';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 3 WHERE type = 'wfw_form' AND template_type = 'wfw_form_1';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 4 WHERE type = 'wfw_form' AND template_type = 'hubei_qun_art_museum';

-- 刷数据
-- 创建一个临时结果集表
CREATE TABLE temp_res (
    id INT ( 11 ),
    pid INT ( 11 )
);

-- 将错误数据纠正后的结果暂存到临时结果集表里面
INSERT INTO temp_res ( id, pid )
SELECT
    id,
    pid
FROM
    (
--         查询t_sign_up_fill_info_type中的报名信息填报的模板组件id及其pid
    SELECT
        t_template_component.id,
        t_template_component.pid
    FROM
        t_sign_up_fill_info_type
        INNER JOIN t_template_component ON t_sign_up_fill_info_type.template_component_id = t_template_component.id
        INNER JOIN t_component ON t_template_component.component_id = t_component.id
        AND t_template_component.component_id IN ( 34, 36 )) tmp
    WHERE
--       查询 t_sign_up_fill_info_type 中已存在的报名信息填报模板组件的pid不存在于t_sign_up_fill_info_type表但其本身存在的数据
    tmp.pid NOT IN (
--             查询报名信息填报中已存在的报名模板组件id
        SELECT DISTINCT
            t_template_component.id
        FROM
            t_sign_up_fill_info_type
            INNER JOIN t_template_component ON t_sign_up_fill_info_type.template_component_id = t_template_component.id
            INNER JOIN t_component ON t_template_component.component_id = t_component.id
            AND t_template_component.component_id IN ( 12, 13 )
    );
-- 	更新错误数据
UPDATE t_sign_up_fill_info_type t
    INNER JOIN temp_res AS res ON t.template_component_id = res.id
    SET t.template_component_id = res.pid;
-- 删除临时存储表
DROP TABLE temp_res;
-- 刷数据

-- 标签
CREATE TABLE `t_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NOT NULL COMMENT '标签名称',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_unique`(`name`)
) COMMENT = '标签表';
CREATE TABLE `t_org_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `fid` int(0) NULL COMMENT '机构id',
    `tag_id` int(0) NULL COMMENT '标签id',
    `sequence` int(0) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`fid`)
) COMMENT = '机构标签表';
CREATE TABLE `t_market_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `tag_id` int(0) NULL COMMENT '标签id',
    `sequence` int(0) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`)
) COMMENT = '活动市场标签表';
CREATE TABLE `t_activity_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(0) NULL COMMENT '活动id',
    `tag_id` int(0) NULL COMMENT '标签id',
    PRIMARY KEY (`id`),
    INDEX `idx_activity`(`activity_id`),
    INDEX `idx_tag`(`tag_id`)
) COMMENT = '活动关联标签表';

-- 浙江省图书馆使用zjlib刷数据
-- 模版
INSERT INTO `activity_engine`.`t_template`(`id`, `name`, `market_id`, `origin_template_id`, `is_system`, `activity_flag`, `fid`, `cover_url`, `sequence`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (12, '浙江省图书馆', NULL, NULL, 1, 'zjlib', NULL, '', 12, '2021-11-24 19:56:33', 172649568, '2021-11-24 19:57:13', 172649568);
-- 组件
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (46, 0, '参与人数', NULL, 1, '请填写真实参与活动的人数', 0, 0, NULL, NULL, NULL, NULL, 12, '2021-11-26 15:17:09', NULL, '2021-11-26 15:17:48', NULL);
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (47, 0, '活动费用', NULL, 1, '请填写本次活动的费用', 0, 0, NULL, NULL, NULL, NULL, 12, '2021-11-26 15:17:11', NULL, '2021-11-26 15:17:46', NULL);
-- 模版组件关联
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (206, 0, 12, 40, '基本信息', '', 0, 1, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (207, 0, 12, 1, '名称', NULL, 1, 2, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (208, 0, 12, 2, '活动时间', NULL, 1, 3, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (209, 0, 12, 6, '分类', NULL, 0, 4, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (210, 0, 12, 3, '封面', NULL, 1, 5, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (211, 0, 12, 4, '主办方', NULL, 0, 6, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (212, 0, 12, 5, '类型', NULL, 0, 7, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (226, 0, 12, 46, '参与人数', '请填写真实参与活动的人数', 0, 8, 0, 'int', NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (227, 0, 12, 47, '活动费用', '请填写本次活动的费用', 0, 9, 0, 'decimal', NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (213, 0, 12, 11, '发布到图书馆', '请选择开展活动的图书馆', 1, 10, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (214, 0, 12, 13, '报名', NULL, 0, 11, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (215, 0, 12, 39, '签到', NULL, 0, 12, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (216, 0, 12, 16, '作品征集', '开启后，参与者可以在活动中提交作品，管理员在征集模块中进行审核评分推优等操作', 0, 13, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (217, 0, 12, 17, '标签', NULL, 0, 14, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (218, 0, 12, 19, '定时发布', '开启后，可以按照设置的时间自动发布活动', 0, 15, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (219, 0, 12, 20, '简介', NULL, 0, 16, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (220, 214, 12, 30, '报名时间', NULL, 1, 17, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (221, 214, 12, 33, '人数限制', NULL, 0, 18, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (222, 214, 12, 34, '报名填报信息', '开启后，报名者需填写信息', 0, 20, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (223, 214, 12, 35, '报名需要审核', '报名需活动管理员审核', 0, 21, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (224, 214, 12, 36, '报名名单公开', '开启后，所有人都能看到报名人员名单', 0, 22, 0, NULL, NULL, NULL, NULL);
INSERT INTO `activity_engine`.`t_template_component`(`id`, `pid`, `template_id`, `component_id`, `name`, `introduction`, `is_required`, `sequence`, `is_deleted`, `type`, `data_origin`, `origin_identify`, `field_flag`) VALUES (225, 214, 12, 37, '取消报名设置', '开启后，指定时间后参与者不能取消报名', 0, 23, 0, NULL, NULL, NULL, NULL);
-- 活动市场添加来源类型、来源
ALTER TABLE t_market ADD origin_type VARCHAR ( 50 ) DEFAULT 'system' COMMENT '来源类型';
ALTER TABLE t_market ADD origin VARCHAR ( 50 ) COMMENT '来源';
UPDATE t_market t SET t.origin_type = 'wfw', t.origin = t.wfw_app_id WHERE t.wfw_app_id IS NOT NULL;
