UPDATE t_user_stat_summary t,
    t_activity t1
SET t.period = t1.period,
    t.credit = t1.credit
WHERE
    t.activity_id = t1.id;

-- 2022-01-18
INSERT INTO `activity_engine`.`t_component` (`id`, `pid`, `name`, `code`, `is_required`, `introduction`, `is_system`, `is_multi`, `type`, `data_origin`, `origin_identify`, `field_flag`, `template_id`, `create_time`, `create_uid`, `update_time`, `update_uid`) VALUES (52, 13, '角色范围', 'sign_up_role_limit', 0, '开启后，报名者需要是对应角色才能报名', 1, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-18 10:48:54', 25418810, '2022-01-18 10:50:35', 25418810);


