# 建表脚本
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists cmoj;

-- 切换库
use cmoj;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;
-- 题目表
create table if not exists question
(
    id         bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    submitNum   int                                default 0  not null comment  '题目提交数',
    acceptedNum int                               default 0   not null comment '题目通过数',
    judgeCase   text                            null comment '判题用例 （json数组）',
    judgeConfig  text                             null comment '判题用例 （json数组）',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;
-- 题目提交表（逻辑删除）
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language      varchar(128)                       not null comment '编程语言',
    code          text                               not null comment '用户代码',
    judgeInfo     text                               null comment '判题信息(jso对象)',
    status        int         default 0              not null comment '判题状态(0 -待判 1-已判)',
    questionId    bigint                             not null comment ' id',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交';
# -- 帖子表
# create table if not exists post
# (
#     id         bigint auto_increment comment 'id' primary key,
#     title      varchar(512)                       null comment '标题',
#     content    text                               null comment '内容',
#     tags       varchar(1024)                      null comment '标签列表（json 数组）',
#     thumbNum   int      default 0                 not null comment '点赞数',
#     favourNum  int      default 0                 not null comment '收藏数',
#     userId     bigint                             not null comment '创建用户 id',
#     createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
#     updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
#     isDelete   tinyint  default 0                 not null comment '是否删除',
#     index idx_userId (userId)
# ) comment '帖子' collate = utf8mb4_unicode_ci;
#
# -- 帖子点赞表（硬删除）
# create table if not exists post_thumb
# (
#     id         bigint auto_increment comment 'id' primary key,
#     postId     bigint                             not null comment '帖子 id',
#     userId     bigint                             not null comment '创建用户 id',
#     createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
#     updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
#     index idx_postId (postId),
#     index idx_userId (userId)
# ) comment '帖子点赞';
#
# -- 帖子收藏表（硬删除）
# create table if not exists post_favour
# (
#     id         bigint auto_increment comment 'id' primary key,
#     postId     bigint                             not null comment '帖子 id',
#     userId     bigint                             not null comment '创建用户 id',
#     createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
#     updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
#     index idx_postId (postId),
#     index idx_userId (userId)
# ) comment '帖子收藏';