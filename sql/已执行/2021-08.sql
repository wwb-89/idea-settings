-- 门户模版添加网站id字段
ALTER TABLE t_web_template ADD website_id INT(11) COMMENT '网站id';
RENAME TABLE t_activity_market TO t_market;
CREATE TABLE `t_org_market_data_push`  (
    `fid` int(11) NULL COMMENT '机构id',
    `market_id` int(11) NULL COMMENT '市场id',
    INDEX `idx_primary`(`fid`)
) COMMENT = '机构市场数据推送';
ALTER TABLE t_org_data_repo_config ADD is_specify_market TINYINT(1) DEFAULT 0 COMMENT "是否指定活动市场";