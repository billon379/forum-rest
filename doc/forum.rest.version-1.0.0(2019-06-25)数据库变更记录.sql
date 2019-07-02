#创建数据库(forum)
CREATE DATABASE IF NOT EXISTS forum;

#切换数据库
USE forum;

#创建论坛话题表(forum_topic)
CREATE TABLE forum_topic
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY
        COMMENT '主鍵',
    title       VARCHAR(50) NOT NULL
        COMMENT '话题',
    create_time DATETIME DEFAULT NOW()
        COMMENT '记录创建时间',
    delete_time DATETIME COMMENT '记录删除时间'
);

#创建论坛帖子表(forum_post)
CREATE TABLE forum_post
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY
        COMMENT '主鍵',
    topic_id     INTEGER NOT NULL
        COMMENT '话题id',
    title        VARCHAR(50) COMMENT '标题',
    content      VARCHAR(1024) COMMENT '内容',
    uid          INTEGER NOT NULL
        COMMENT '用户id',
    lat          DECIMAL(18, 6)
        COMMENT '纬度',
    lng          DECIMAL(18, 6)
        COMMENT '经度',
    address      VARCHAR(100) COMMENT '位置',
    status       TINYINT  DEFAULT 1
        COMMENT '发布状态(1:审核中;2:审核通过;3:审核未通过)',
    `limit`      TINYINT  DEFAULT 1
        COMMENT '限制(1:免费观看;2:分享后可见;3:付费后可见)',
    price        DECIMAL(18, 1) COMMENT '价格',
    expired_time DATETIME COMMENT '过期时间',
    payment      TINYINT COMMENT '支付方式(1:金币;2:现金)',
    create_time  DATETIME DEFAULT NOW()
        COMMENT '记录创建时间',
    delete_time  DATETIME COMMENT '记录删除时间'
);

#创建论坛媒体表(forum_media)
CREATE TABLE forum_media
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY
        COMMENT '主鍵',
    post_id     INTEGER            NOT NULL
        COMMENT '关联的帖子id',
    type        TINYINT  DEFAULT 1 NOT NULL
        COMMENT '文件类型(1:图片;2:视频)',
    cover       VARCHAR(255) COMMENT '视频封面',
    url         VARCHAR(255)       NOT NULL
        COMMENT '文件地址',
    `desc`      VARCHAR(255) COMMENT '文件描述',
    sort        TINYINT COMMENT '排序',
    visiable    TINYINT  DEFAULT 1
        COMMENT '是否可见(1:可见;2:不可见)',
    create_time DATETIME DEFAULT NOW()
        COMMENT '记录创建时间',
    delete_time DATETIME COMMENT '记录删除时间'
);

#创建论坛评论表(forum_reply)
CREATE TABLE forum_reply
(
    id          INTEGER AUTO_INCREMENT PRIMARY KEY
        COMMENT '主鍵',
    post_id     INTEGER NOT NULL
        COMMENT '关联的帖子id',
    uid         INTEGER NOT NULL
        COMMENT '用户id',
    ref_id      INTEGER NULL
        COMMENT '关联的评论id',
    content     VARCHAR(255) COMMENT '评论内容',
    create_time DATETIME DEFAULT NOW()
        COMMENT '记录创建时间',
    delete_time DATETIME COMMENT '记录删除时间'
);

#创建帖子扩展信息表(forum_post_extend)
CREATE TABLE forum_post_extend
(
    post_id    INTEGER PRIMARY KEY
        COMMENT '关联的帖子id',
    read_num   INTEGER DEFAULT 0
        COMMENT '阅读数',
    praise_num INTEGER DEFAULT 0
        COMMENT '点赞数',
    shared_num INTEGER DEFAULT 0
        COMMENT '分享数',
    reply_num  INTEGER DEFAULT 0
        COMMENT '评论数',
    pay_num    INTEGER DEFAULT 0
        COMMENT '购买数'
);

#创建帖子解锁信息表(forum_post_unlock)
CREATE TABLE forum_post_unlock
(
    id            INTEGER AUTO_INCREMENT PRIMARY KEY
        COMMENT '主鍵',
    post_id       INTEGER NOT NULL
        COMMENT '帖子id',
    uid           INTEGER NOT NULL
        COMMENT '用户id',
    unlock_method TINYINT NOT NULL
        COMMENT '解锁方式(1:分享;2:付费)',
    create_time   DATETIME DEFAULT NOW()
        COMMENT '记录创建时间',
    delete_time   DATETIME COMMENT '记录删除时间'
);