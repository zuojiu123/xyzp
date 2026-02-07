-- 添加type字段到employment_user表
USE `campus_recruitment`;

ALTER TABLE `employment_user` ADD COLUMN `type` int(11) DEFAULT 1 COMMENT '类型：1-投递，2-收藏' AFTER `reply_user`;