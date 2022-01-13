UPDATE t_user_stat_summary t,
    t_activity t1
SET t.period = t1.period,
    t.credit = t1.credit
WHERE
    t.activity_id = t1.id;


