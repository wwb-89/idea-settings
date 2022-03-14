-- 2022-03-14
ALTER TABLE t_certificate_issue ADD qr_code_status INT DEFAULT 2 COMMENT '二维码状态';
ALTER TABLE t_certificate_issue ADD qr_code_cloud_id VARCHAR(50) COMMENT '二维码云盘id';
