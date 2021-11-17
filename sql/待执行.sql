CREATE TABLE `t_market_sign_up_config`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `sign_up_activity_limit` int(11) NULL DEFAULT 0 COMMENT '同时报名的活动上限',
    `sign_up_btn_name` varchar(50) NULL DEFAULT '报名参与' COMMENT '报名按钮名称',
    `sign_up_key_word` varchar(50) NULL DEFAULT '报名' COMMENT '报名关键字',
    PRIMARY KEY (`id`)
) COMMENT = '活动市场报名配置表';
INSERT INTO t_market_sign_up_config ( market_id, sign_up_activity_limit ) SELECT t.id, t.sign_up_activity_limit FROM t_market t;