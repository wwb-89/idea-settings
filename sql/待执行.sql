-- 2022-03-21
ALTER TABLE t_template_component ADD is_not_match_show_tips TINYINT DEFAULT 0 COMMENT '不满足条件时是否提示文字';
ALTER TABLE t_template_component ADD not_match_tips varchar(255) DEFAULT "" COMMENT '不满足条件时的提示文字';
ALTER TABLE t_template_component ADD is_not_match_jump TINYINT DEFAULT 0 COMMENT '不满足条件时是否点击跳转';
ALTER TABLE t_template_component ADD not_match_jump_url varchar(255) DEFAULT "" COMMENT '不满足条件时跳转链接';