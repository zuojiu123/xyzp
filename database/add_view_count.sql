-- 为article表添加view_count字段
ALTER TABLE `article` ADD COLUMN `view_count` INT(11) DEFAULT 0 COMMENT '阅读数' AFTER `thumb_up`;