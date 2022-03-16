-- 组件表增加是否开关字段判断
ALTER TABLE t_component ADD is_switch_btn TINYINT(1) DEFAULT 0 COMMENT '是否开关组件，0：否，1：是';
-- 模板组件关联表增加开关对应属性值
ALTER TABLE t_template_component ADD is_show TINYINT(1) DEFAULT 1 COMMENT '是否显示，0：否，1：是';
ALTER TABLE t_template_component ADD is_disabled TINYINT(1) DEFAULT 0 COMMENT '是否禁用，0：否，1：是';
ALTER TABLE t_template_component ADD is_open TINYINT(1) DEFAULT 0 COMMENT '是否默认开启，0：否，1：是';
-- 更新component组件中switch组件
update t_component set is_switch_btn = 1 where `code` in ('work', 'activity_rating',
    'timing_release', 'reading', 'group', 'inspection_config', 'clazz_interaction',
    'push_reminder', 'company_sign_up', 'sign_up', 'sign_up_fill_info', 'sign_up_review',
    'sign_up_public_list', 'sign_up_cancel_signed_up', 'sign_up_condition', 'on_site_sign_up');

