use excited_oj_dev;

create table if not exists tb_sys_user (
    user_id bigint unsigned not null comment '用户id',
    user_account varchar(20) not null comment '账号',
    nick_name varchar(20) comment '昵称',
    password char(60) not null comment '密码',
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',
    primary key (user_id),
    unique key `idx_user_account` (user_account)
);

create table if not exists tb_question (
    question_id bigint unsigned not null comment '题目id',
    title varchar(50) not null comment '题目标题',
    content varchar(1000) not null comment '题目内容',
    difficulty tinyint not null comment '题目难度, 1-简单 2-中等 3-困难',
    time_limit int not null comment '时间限制',
    space_limit int not null comment '空间限制',
    question_case varchar(1000) comment '题目用例',
    default_code varchar(500) comment '默认代码块',
    main_func varchar(500) comment 'main方法',
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',
    primary key (question_id)
);

create table if not exists tb_exam (
    exam_id bigint unsigned not null comment '竞赛id',
    title varchar(50) not null comment '竞赛标题',
    start_time datetime not null comment '开始时间',
    end_time datetime not null comment '结束时间',
    status tinyint not null default '0' comment '发布状态: 0-未发布, 1-已发布',
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',
    primary key (exam_id)
);

create table if not exists tb_exam_question (
    exam_question_id bigint unsigned not null comment '竞赛题目id',
    exam_id bigint unsigned not null comment '竞赛id',
    question_id bigint unsigned not null comment '题目id',
    question_order int not null comment '题目顺序',
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',
    primary key (exam_question_id)
);

create table if not exists tb_user(
    user_id bigint unsigned not null comment '用户id',
    sex tinyint comment '性别, 1-男, 2-女',
    head_image varchar(100) comment '头像',
    nick_name varchar(20) comment '用户昵称',
    phone char(11) not null comment '电话号码',
    code char(6) comment '验证码',
    email varchar(20) comment '邮箱',
    wechat varchar(20) comment '微信',
    school_name varchar(20) comment '学校',
    major_name varchar(20) comment '专业',
    introduce varchar(100) comment '个人介绍',
    status tinyint not null comment '用户状态, 0-正常, 1-拉黑',
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '修改人',
    update_time datetime comment '修改时间',
    primary key (user_id)
);