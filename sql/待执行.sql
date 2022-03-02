-- 2022-03-01
CREATE TABLE `t_blacklist_detail` (
     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
     `market_id` int(11) DEFAULT NULL COMMENT '市场id',
     `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
     `uid` int(11) DEFAULT NULL COMMENT '用户id',
     `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
     `account` varchar(50) DEFAULT NULL COMMENT '账号',
     `breach_content` varchar(255) DEFAULT NULL COMMENT '违约信息',
     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '违约时间',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='黑名单违约记录明细';