-- 进入黑名单系统通知模板文案调整
UPDATE t_system_notice_template
SET `content` = '您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，在此期间内将无法参加任何该单位活动。<div>未签到/签退活动名称：<input type="text" class="tag" readonly="readonly" value="活动名称" code="activity_name" style="width: 55px;"></div><div>活动时间：<input type="text" class="tag" readonly="readonly" value="活动名称" code="activity_name" style="width: 55px;"></div>',
    `code_content` = '您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，

未签到/签退活动名称：{activity_name}
活动时间：{activity_time}
'
WHERE
        id = 6
  AND notice_type = 'auto_add_to_blacklist';
UPDATE t_system_notice_template
SET `content` = '您好，您已被<input type="text" class="tag" readonly="readonly" value="活动主办方" code="activity_organisers" style="width: 65px;">拉入黑名单。',
    `code_content` = '您好，您已被{activity_organisers}拉入黑名单。'
WHERE
        id = 7
  AND notice_type = 'manual_add_to_blacklist';

-- 2021-12-15
ALTER TABLE t_activity ADD certificate_template_id INT ( 11 ) COMMENT '证书模版id';
INSERT INTO `activity_engine`.`t_component`(`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (49, 0, '证书设置', 'certificate', 1, '可配置一个证书，可以给参与者发放证书', 1, 0, NULL, NULL, NULL, NULL, NULL, '2021-12-15 15:52:49', 25418810, '2021-12-15 15:54:04', 25418810);
ALTER TABLE t_inspection_config ADD is_auto_issue_certificate TINYINT(1) DEFAULT 0 COMMENT '是否自动发放证书';
CREATE TABLE `t_certificate_issue` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
    `uid` int(11) DEFAULT NULL COMMENT '发放用户id',
    `no` varchar(50) DEFAULT NULL COMMENT '证书编号',
    `serial_no` int(11) DEFAULT NULL COMMENT '序号',
    `issue_time` datetime DEFAULT NULL COMMENT '发放时间',
    `create_time` datetime DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_primary` (`activity_id`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='证书发放表';

ALTER TABLE t_user_result ADD real_name VARCHAR(50) COMMENT '姓名';
ALTER TABLE t_user_result ADD uname VARCHAR(50) COMMENT '用户名';
