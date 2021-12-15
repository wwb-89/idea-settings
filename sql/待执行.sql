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

-- 补充主键sql
ALTER TABLE t_activity_area_flag ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_class ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_manager ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_scope ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_sign_module ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_stat_summary ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_stat_task_detail ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_table_field ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_market_classify ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_market_table_field ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_org_classify ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_org_market_data_push ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_org_table_field ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_user_stat_summary ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE t_activity_stat ADD id INT(11) PRIMARY KEY AUTO_INCREMENT;
