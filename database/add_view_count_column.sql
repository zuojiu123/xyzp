-- 为company表添加view_count字段，用于记录人气热度（阅读次数）
ALTER TABLE `campus_recruitment`.`company` ADD COLUMN `view_count` INT(11) DEFAULT 0 COMMENT '人气热度（阅读次数）' AFTER `company_logo`;