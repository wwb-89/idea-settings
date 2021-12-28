-- 恢复报名即将开始和报名即将结束通知模板
UPDATE t_system_notice_template SET is_deleted = 0 WHERE id IN (4, 5);