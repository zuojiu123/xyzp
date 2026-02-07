-- 校园招聘系统数据库（修正版）
CREATE DATABASE IF NOT EXISTS `campus_recruitment` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_recruitment`;

-- 用户表（修正版 - 添加role字段和其他缺失字段）
CREATE TABLE `user` (
                        `id` varchar(32) NOT NULL COMMENT '用户ID',
                        `user_name` varchar(50) NOT NULL COMMENT '用户名',
                        `password` varchar(255) NOT NULL COMMENT '密码',
                        `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
                        `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
                        `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                        `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                        `identity_num` varchar(18) DEFAULT NULL COMMENT '身份证号',
                        `address` varchar(255) DEFAULT NULL COMMENT '居住地址',
                        `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
                        `role` varchar(20) NOT NULL DEFAULT '0' COMMENT '用户角色：Normal_User-普通用户，Admin-管理员，Enterprise_User-企业用户',
                        `integral` int(11) DEFAULT 500 COMMENT '积分',
                        `credit` int(11) DEFAULT 5 COMMENT '信誉度',
                        `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                        `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
                        `deleted` int(11) DEFAULT 0 COMMENT '逻辑删除',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_user_name` (`user_name`),
                        UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 公司表（修正版 - 添加缺失字段）
CREATE TABLE `company` (
                           `id` varchar(32) NOT NULL COMMENT '公司ID',
                           `company_number` varchar(50) DEFAULT NULL COMMENT '经营许可证编号',
                           `name` varchar(100) NOT NULL COMMENT '公司名称',
                           `description` text COMMENT '公司描述',
                           `company_logo` varchar(255) DEFAULT NULL COMMENT '公司Logo',
                           `user_id` varchar(32) NOT NULL COMMENT '用户ID',
                           `status` varchar(20) DEFAULT 'Check_Pending' COMMENT '审核状态：Check_Pending-待审核，Approve-审核通过，Audit_Failed-审核失败',
                           `category` varchar(20) DEFAULT NULL COMMENT '公司类别',
                           `nature` varchar(20) DEFAULT NULL COMMENT '公司性质',
                           `number` int(11) DEFAULT NULL COMMENT '公司人数',
                           `processor` varchar(32) DEFAULT NULL COMMENT '审核人',
                           `reply_time` bigint(20) DEFAULT NULL COMMENT '回复时间',
                           `reply_content` text COMMENT '回复内容',
                           `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                           `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司表';

-- 职位表（修正版 - 添加缺失字段）
CREATE TABLE `employment` (
                              `id` varchar(32) NOT NULL COMMENT '职位ID',
                              `user_id` varchar(32) NOT NULL COMMENT '用户ID',
                              `company_id` varchar(32) NOT NULL COMMENT '公司ID',
                              `title` varchar(100) NOT NULL COMMENT '职位标题',
                              `description` text COMMENT '职位描述',
                              `position` varchar(100) DEFAULT NULL COMMENT '工作地点',
                              `education` varchar(20) DEFAULT NULL COMMENT '学历要求',
                              `min_salary` varchar(20) DEFAULT NULL COMMENT '最低薪资',
                              `max_salary` varchar(20) DEFAULT NULL COMMENT '最高薪资',
                              `salary` varchar(50) DEFAULT NULL COMMENT '薪资范围',
                              `treatment` text COMMENT '福利待遇',
                              `status` int(11) DEFAULT 0 COMMENT '状态：0-待审核，1-审核通过，2-驳回',
                              `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                              `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_company_id` (`company_id`),
                              KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- 求职申请表（修正版 - 添加缺失字段）
CREATE TABLE `employment_user` (
                                   `id` varchar(32) NOT NULL COMMENT '申请ID',
                                   `employment_id` varchar(32) NOT NULL COMMENT '职位ID',
                                   `user_id` varchar(32) NOT NULL COMMENT '用户ID',
                                   `name` varchar(50) NOT NULL COMMENT '申请人姓名',
                                   `phone` varchar(20) NOT NULL COMMENT '联系电话',
                                   `introduce` text COMMENT '自我介绍',
                                   `resume` varchar(255) DEFAULT NULL COMMENT '简历文件路径',
                                   `reply_status` varchar(20) DEFAULT 'Wait_For_Reply' COMMENT '回复状态：Wait_For_Reply-等待回复，Agree_With_Induction-同意入职',
                                   `user_status` varchar(20) DEFAULT 'Normal' COMMENT '用户状态',
                                   `reply_user` varchar(32) DEFAULT NULL COMMENT '回复人',
                                   `reply_content` text COMMENT '回复内容',
                                   `reply_time` bigint(20) DEFAULT NULL COMMENT '回复时间',
                                   `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                                   `deleted` int(11) DEFAULT 0 COMMENT '逻辑删除',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_employment_id` (`employment_id`),
                                   KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求职申请表';

-- 文章表（修正版 - 添加缺失字段）
CREATE TABLE `article` (
                           `id` varchar(32) NOT NULL COMMENT '文章ID',
                           `title` varchar(200) NOT NULL COMMENT '文章标题',
                           `content` longtext COMMENT '文章内容',
                           `pic_path` varchar(255) DEFAULT NULL COMMENT '封面图片',
                           `user_id` varchar(32) NOT NULL COMMENT '发布用户ID',
                           `type` varchar(20) DEFAULT NULL COMMENT '文章类型',
                           `collect_number` int(11) DEFAULT 0 COMMENT '收藏数',
                           `thumb_up` int(11) DEFAULT 0 COMMENT '点赞数',
                           `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                           `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 评论表
CREATE TABLE `comments` (
                            `id` varchar(32) NOT NULL COMMENT '评论ID',
                            `article_id` varchar(32) NOT NULL COMMENT '文章ID',
                            `content` text NOT NULL COMMENT '评论内容',
                            `recipient_id` varchar(32) DEFAULT NULL COMMENT '被评论人ID',
                            `operator_id` varchar(32) NOT NULL COMMENT '评论人ID',
                            `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 回复表
CREATE TABLE `reply_info` (
                              `id` varchar(32) NOT NULL COMMENT '回复ID',
                              `content` text NOT NULL COMMENT '回复内容',
                              `recipient_id` varchar(32) DEFAULT NULL COMMENT '被回复人ID',
                              `operator_id` varchar(32) NOT NULL COMMENT '回复人ID',
                              `comments_id` varchar(32) NOT NULL COMMENT '评论ID',
                              `parent_reply_id` varchar(32) DEFAULT NULL COMMENT '父回复ID',
                              `atid` varchar(32) DEFAULT NULL COMMENT '@用户ID',
                              `type` varchar(10) DEFAULT NULL COMMENT '回复类型',
                              `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_comments_id` (`comments_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复表';

-- 评论回复关系表
CREATE TABLE `comment_reply_relation` (
                                          `id` varchar(32) NOT NULL COMMENT '关系ID',
                                          `reply_info_id` varchar(32) NOT NULL COMMENT '回复ID',
                                          `comments_id` varchar(32) NOT NULL COMMENT '评论ID',
                                          `parent_reply_id` varchar(32) DEFAULT NULL COMMENT '父回复ID',
                                          `type` int(11) DEFAULT 1 COMMENT '类型',
                                          `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论回复关系表';

-- 用户简历表
CREATE TABLE `user_resume` (
                               `id` varchar(32) NOT NULL COMMENT '简历ID',
                               `user_id` varchar(32) NOT NULL COMMENT '用户ID',
                               `resume_name` varchar(100) NOT NULL COMMENT '简历名称',
                               `resume_url` varchar(255) NOT NULL COMMENT '简历文件路径',
                               `type` varchar(20) DEFAULT NULL COMMENT '文件类型',
                               `extension` varchar(10) DEFAULT NULL COMMENT '文件扩展名',
                               `resume_type` int(11) DEFAULT 1 COMMENT '简历类型',
                               `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户简历表';

-- 反馈表（修正版 - 添加缺失字段）
CREATE TABLE `feedback` (
                            `id` varchar(32) NOT NULL COMMENT '反馈ID',
                            `title` varchar(200) NOT NULL COMMENT '反馈标题',
                            `content` text NOT NULL COMMENT '反馈内容',
                            `user_id` varchar(32) NOT NULL COMMENT '反馈用户ID',
                            `type` int(11) NOT NULL DEFAULT 0 COMMENT '反馈类型：1bug反馈，2建议，3投诉',
                            `status` int(11) DEFAULT 0 COMMENT '处理状态：0未回复，1已回复',
                            `reply_content` text COMMENT '回复内容',
                            `reply_user_id` varchar(32) DEFAULT NULL COMMENT '回复用户ID',
                            `reply_time` bigint(20) DEFAULT NULL COMMENT '回复时间',
                            `create_time` bigint(20) NOT NULL COMMENT '创建时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表';

-- 地区选择表
CREATE TABLE `area_selection` (
                                  `id` varchar(32) NOT NULL COMMENT '地区ID',
                                  `code` varchar(20) NOT NULL COMMENT '行政区划代码',
                                  `name` varchar(50) NOT NULL COMMENT '地区名称',
                                  `parent_code` varchar(20) DEFAULT NULL COMMENT '上级代码',
                                  `level` varchar(10) DEFAULT NULL COMMENT '级别',
                                  `hot` int(11) DEFAULT 0 COMMENT '是否热门城市',
                                  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                                  `sequence` varchar(10) DEFAULT NULL COMMENT '排序',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地区选择表';
