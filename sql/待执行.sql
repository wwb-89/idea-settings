ALTER TABLE t_sign_up_fill_info_type ADD wfw_form_template_id INT(11) COMMENT '报名万能表单模板id';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 1 WHERE type = 'wfw_form' AND template_type = 'normal';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 2 WHERE type = 'wfw_form' AND template_type = 'dual_select_company';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 3 WHERE type = 'wfw_form' AND template_type = 'wfw_form_1';
UPDATE t_sign_up_fill_info_type SET wfw_form_template_id = 4 WHERE type = 'wfw_form' AND template_type = 'hubei_qun_art_museum';

-- 刷数据
-- 创建一个临时结果集表
CREATE TABLE temp_res (
    id INT ( 11 ),
    pid INT ( 11 ));

-- 将错误数据纠正后的结果暂存到临时结果集表里面
INSERT INTO temp_res ( id, pid ) SELECT
    id,
    pid
FROM
    (
--         查询t_sign_up_fill_info_type中的报名信息填报的模板组件id及其pid
        SELECT
            t_template_component.id,
            t_template_component.pid
        FROM
            t_sign_up_fill_info_type
                INNER JOIN t_template_component ON t_sign_up_fill_info_type.template_component_id = t_template_component.id
                INNER JOIN t_component ON t_template_component.component_id = t_component.id
                AND t_template_component.component_id IN ( 34, 36 )) tmp
WHERE
--       查询 t_sign_up_fill_info_type 中已存在的报名信息填报模板组件的pid不存在于t_sign_up_fill_info_type表但其本身存在的数据
        tmp.pid NOT IN (
--             查询报名信息填报中已存在的报名模板组件id
        SELECT DISTINCT
            t_template_component.id
        FROM
            t_sign_up_fill_info_type
                INNER JOIN t_template_component ON t_sign_up_fill_info_type.template_component_id = t_template_component.id
                INNER JOIN t_component ON t_template_component.component_id = t_component.id
                AND t_template_component.component_id IN ( 12, 13 ));
-- 	更新错误数据
UPDATE t_sign_up_fill_info_type t
    INNER JOIN temp_res AS res ON t.template_component_id = res.id
    SET t.template_component_id = res.pid;
-- 删除临时存储表
DROP TABLE temp_res;
-- 刷数据

-- 标签
CREATE TABLE `t_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(50) NOT NULL COMMENT '标签名称',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_unique`(`name`)
) COMMENT = '标签表';
CREATE TABLE `t_org_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `fid` int(0) NULL COMMENT '机构id',
    `tag_id` int(0) NULL COMMENT '标签id',
    `sequence` int(0) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`fid`)
) COMMENT = '机构标签表';
CREATE TABLE `t_market_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `market_id` int(0) NULL COMMENT '活动市场id',
    `tag_id` int(0) NULL COMMENT '标签id',
    `sequence` int(0) NULL COMMENT '顺序',
    `create_time` datetime(0) NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `update_time` datetime(0) NULL DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_primary`(`market_id`)
) COMMENT = '活动市场标签表';
CREATE TABLE `t_activity_tag`  (
    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` int(0) NULL COMMENT '活动id',
    `tag_id` int(0) NULL COMMENT '标签id',
    PRIMARY KEY (`id`),
    INDEX `idx_activity`(`activity_id`),
    INDEX `idx_tag`(`tag_id`)
) COMMENT = '活动关联标签表';

