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