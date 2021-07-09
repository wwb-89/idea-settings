CREATE TABLE `t_template`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NULL COMMENT '模版名称',
    `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否是系统模版',
    `fid` int(11) NULL COMMENT '所属机构id',
    `cover_url` varchar(255) NULL COMMENT '封面url',
    `sequence` int(11) NULL DEFAULT 1 COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`is_system`, `fid`)
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
    `fid` int(11) NULL COMMENT '所属机构id',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `create_uid` int(11) NULL COMMENT '创建人uid',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    `update_uid` int(11) NULL COMMENT '更新人uid',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`fid`),
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
    `template_id` int(11) NULL COMMENT '模版id',
    `component_id` int(11) NULL COMMENT '组件id',
    `name` varchar(50) NULL COMMENT '定制的名称',
    `introduction` varchar(255) NULL COMMENT '定制的简介',
    `is_required` tinyint(1) NULL DEFAULT 1 COMMENT '是否必填',
    `sequence` int(11) NULL COMMENT '顺序',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_id`)
) COMMENT = '模版组件关联表';
CREATE TABLE `t_sign_up_condition`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id` int(11) NULL COMMENT '模版id',
    `component_id` int(11) NULL COMMENT '组件id',
    `origin_identify` varchar(50) NULL COMMENT '来源主键标识',
    `filed_name` varchar(50) NULL COMMENT '字段名',
    `is_allow_signed_up` tinyint(1) NULL DEFAULT 1 COMMENT '是否允许报名',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_id`, `component_id`)
) COMMENT = '模版组件报名条件表';

CREATE TABLE `t_sign_up_fill_info_type`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id` int(11) NULL COMMENT '模版id',
    `component_id` int(11) NULL COMMENT '组件id',
    `type` varchar(50) NULL COMMENT '类型',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`template_id`, `component_id`)
) COMMENT = '报名填报信息类型表：默认、双选会、微服务表单';
CREATE TABLE `t_activity_component_value`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) NULL COMMENT '活动id',
    `component_id` int(11) NULL COMMENT '组件id',
    `value` varchar(255) NULL COMMENT '值',
    PRIMARY KEY (`id`)
) COMMENT = '活动组件值表';