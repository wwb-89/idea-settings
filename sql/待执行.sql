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
