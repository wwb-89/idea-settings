CREATE TABLE `t_template`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NULL COMMENT '模版名称',
    `market_id` int(11) NULL COMMENT '市场id',
    `origin_template_id` int(11) NULL COMMENT '源模版id',
    `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否是系统模版',
    `activity_flag` VARCHAR (50) NULL COMMENT '活动标识',
    `fid` int(11) NULL COMMENT '所属机构id',
    `cover_url` varchar(255) NULL COMMENT '封面url',
    `sequence` int(11) NULL DEFAULT 1 COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`is_system`, `fid`),
    INDEX `idx_market_id`(`market_id`)
) COMMENT = '模版表';
CREATE TABLE `t_component`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pid` int(11) NULL DEFAULT 0 COMMENT '父组件id',
    `market_id` int(11) NULL COMMENT '市场id。系统组件为空',
    `name` varchar(50) NULL COMMENT '组件名称',
    `code` varchar(50) NULL COMMENT '组件编码。系统字段才有code',
    `is_required` tinyint(1) NULL DEFAULT 1 COMMENT '是否必填',
    `introduction` varchar(255) NULL COMMENT '简介',
    `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否是系统组件',
    `is_multi` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持多个组件',
    `type` varchar(50) NULL COMMENT '组件类型。自定义组件才有类型：文本、单选、多选',
    `data_origin` varchar(50) NULL COMMENT '数据来源',
    `origin_identify` varchar(50) NULL COMMENT '来源主键',
    `field_flag` varchar(50) NULL COMMENT '字段标识',
    `template_id` int(11) NULL COMMENT '组件id',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_id`),
    INDEX `idx_system`(`is_system`),
    INDEX `idx_pid`(`pid`)
) COMMENT = '组件表';
CREATE TABLE `t_component_field`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `component_id` int(11) NULL COMMENT '组件id',
    `field_name` varchar(255) NULL COMMENT '字段名',
    `sequence` int(11) NULL COMMENT '顺序',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`component_id`)
) COMMENT = '组件选项表（单选、多选）';
CREATE TABLE `t_template_component`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pid` int(11) NULL COMMENT '父id',
    `template_id` int(11) NULL COMMENT '模版id',
    `component_id` int(11) NULL COMMENT '组件id',
    `name` varchar(50) NULL COMMENT '定制的名称',
    `introduction` varchar(255) NULL COMMENT '定制的简介',
    `is_required` tinyint(1) NULL DEFAULT 0 COMMENT '是否必填',
    `sequence` int(11) NULL COMMENT '顺序',
    `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_id`)
) COMMENT = '模版组件关联表';
CREATE TABLE `t_sign_up_condition`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `fid` int(11) NULL COMMENT '机构id',
    `template_component_id` int(11) NULL COMMENT '模版组件id',
    `origin_identify` varchar(50) NULL COMMENT '来源主键标识',
    `field_name` varchar(50) NULL COMMENT '字段名',
    `is_allow_signed_up` tinyint(1) NULL DEFAULT 1 COMMENT '是否允许报名',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_component_id`)
) COMMENT = '模版组件报名条件表';

CREATE TABLE `t_sign_up_fill_info_type`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_component_id` int(11) NULL COMMENT '模版组件id',
    `type` varchar(50) NULL COMMENT '类型',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_component_id`)
) COMMENT = '报名填报信息类型表：默认、双选会、微服务表单';
CREATE TABLE `t_activity_component_value`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `template_component_id` int(11) NULL COMMENT '模版组件id',
    `template_id` int(11) NULL COMMENT '模版id',
    `component_id` int(11) NULL COMMENT '组件id',
    `value` varchar(255) NULL COMMENT '值',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `template_component_id`),
    INDEX `idx_template_component`(`activity_id`, `template_id`, `component_id`)
) COMMENT = '活动组件值表';
ALTER TABLE t_activity ADD COLUMN market_id INT COMMENT '市场id';
ALTER TABLE t_activity ADD COLUMN template_id INT COMMENT '模版id';
ALTER TABLE t_activity DROP COLUMN is_enable_sign;
ALTER TABLE t_activity DROP COLUMN is_open_audit;
ALTER TABLE t_activity DROP COLUMN audit_status;
ALTER TABLE t_activity DROP COLUMN second_classroom_flag;
ALTER TABLE t_activity_market DROP COLUMN activity_flags;
ALTER TABLE t_market_table_field DROP COLUMN fid;
ALTER TABLE t_activity CHANGE integral_value integral DECIMAL(20, 2) COMMENT '积分';
TRUNCATE TABLE t_market_table_field;
ALTER TABLE t_market_table_field CHANGE activity_flag market_id INT COMMENT '市场id';
-- 不给已创建活动的机构创建活动市场和模版， 先将活动关联上系统模版
UPDATE t_activity t,
    t_template t1
SET t.template_id = t1.id
WHERE
    t1.is_system = 1
  AND t.activity_flag = t1.activity_flag;
-- 活动分类相关
CREATE TABLE `t_classify`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NULL COMMENT '分类名称',
    `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否是系统分类',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`name`)
) COMMENT = '活动分类表';
CREATE TABLE `t_org_classify`  (
    `fid` int(11) NULL COMMENT '机构id',
    `classify_id` int(11) NULL COMMENT '分类id',
    `sequence` int(11) NULL DEFAULT 1 COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间'
);
CREATE TABLE `t_market_classify`  (
    `market_id` int(11) NULL COMMENT '活动市场id',
    `classify_id` int(11) NULL COMMENT '分类id',
    `sequence` int(11) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间'
) COMMENT = '活动市场与活动分类关联表';
-- 刷入系统分类
INSERT INTO t_classify ( `name`, is_system )
SELECT
    t.`name`,
    1
FROM
    t_activity_classify t
WHERE
        t.is_system = 1;
-- 刷入机构创建的分类
INSERT INTO t_classify ( `name`, is_system )
SELECT DISTINCT
    t.`name`,
    0
FROM
    t_activity_classify t
WHERE
    NOT EXISTS (
            SELECT
                1
            FROM
                t_classify t1
            WHERE
                    t.`name` = t1.`name`
        );
-- 输入org_classify表数据
INSERT INTO t_org_classify ( fid, classify_id, sequence )
SELECT
    t.affiliation_fid,
    t1.id,
    t.sequence
FROM
    t_activity_classify t
        INNER JOIN t_classify t1 ON t.`name` = t1.`name`
WHERE
        t.is_system = 0
  AND t.`status` = 1
ORDER BY
    t.affiliation_fid,
    t.sequence;
-- 更新活动表的活动分类id
UPDATE t_activity t,
    (
    SELECT
    t.id,
    t2.id new_classify_id
    FROM
    t_activity t
    INNER JOIN t_activity_classify t1 ON t.activity_classify_id = t1.id
    INNER JOIN t_classify t2 ON t1.`name` = t2.`name`
    ) t1
SET t.activity_classify_id = t1.new_classify_id
WHERE
    t.id = t1.id;
-- 报名条件启用表
CREATE TABLE `t_sign_up_condition_enable`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(0) NULL COMMENT '活动id',
    `template_component_id` int(0) NULL COMMENT '报名条件模版组件id',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`activity_id`, `template_component_id`)
) COMMENT = '报名条件启用表';
-- 黑名单
CREATE TABLE `t_blacklist`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) NULL COMMENT '市场id',
    `uid` int(11) NULL COMMENT '用户id',
    `user_name` varchar(50) NULL COMMENT '姓名',
    `account` varchar(50) NULL COMMENT '账号',
    `default_num` int(11) NULL COMMENT '违约次数',
    `join_type` varchar(50) NULL COMMENT '加入方式',
    `effective_hours` int(11) NULL COMMENT '有效小时数',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`, `uid`)
) COMMENT = '黑名单表';

CREATE TABLE `t_blacklist_record`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) NULL COMMENT '市场id',
    `uid` int(11) NULL COMMENT '用户id',
    `user_name` varchar(50) NULL COMMENT '姓名',
    `account` varchar(50) NULL COMMENT '账号',
    `activity_id` int(11) NULL COMMENT '活动id',
    `not_signed_in_num` int(11) NULL COMMENT '未签次数',
    `is_handled` tinyint(1) NULL DEFAULT 0 COMMENT '是否已处理',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`, `uid`)
) COMMENT = '黑名单记录表';

CREATE TABLE `t_blacklist_rule`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(11) NULL COMMENT '活动市场id',
    `not_sign_in_upper_limit` int(11) NULL COMMENT '未签到上限',
    `is_enable_auto_remove` tinyint(1) NULL COMMENT '是否启用自动移除',
    `auto_remove_hours` int(11) NULL COMMENT '自动移除小时数',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`)
) COMMENT = '黑名单规则表';
