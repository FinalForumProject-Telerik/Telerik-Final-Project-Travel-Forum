create table tags
(
    id   int auto_increment
        primary key,
    name varchar(64) not null,
    constraint name
        unique (name)
);

create table users
(
    id            int auto_increment
        primary key,
    username      varchar(32)          not null,
    first_name    varchar(32)          not null,
    last_name     varchar(32)          not null,
    email         varchar(255)         not null,
    password      varchar(255)         not null,
    phone_number  varchar(32)          null,
    profile_photo varchar(1024)        null,
    is_admin      tinyint(1) default 0 not null,
    is_blocked    tinyint(1) default 0 not null,
    constraint email
        unique (email),
    constraint username
        unique (username),
    constraint check_size_first_name
        check (char_length(`first_name`) between 4 and 32),
    constraint check_size_last_name
        check (char_length(`last_name`) between 4 and 32),
    constraint check_size_username
        check (char_length(`username`) between 4 and 32)
);

create table posts
(
    id         int auto_increment
        primary key,
    user_id    int                                  not null,
    title      varchar(255)                         not null,
    content    text                                 not null,
    created_at datetime default current_timestamp() not null,
    updated_at datetime default current_timestamp() not null on update current_timestamp(),
    likes      int      default 0                   not null,
    constraint posts_users_id_fk
        foreign key (user_id) references users (id),
    constraint check_content_size
        check (char_length(`content`) between 32 and 8192),
    constraint check_title_size
        check (char_length(`title`) between 16 and 64)
);

create table comments
(
    id         int auto_increment
        primary key,
    post_id    int                                  not null,
    user_id    int                                  null,
    content    text                                 not null,
    created_at datetime default current_timestamp() not null,
    constraint comments_fk_post_id
        foreign key (post_id) references posts (id),
    constraint comments_fk_user_id
        foreign key (user_id) references users (id),
    constraint check_comment_content_size
        check (char_length(`content`) between 1 and 2048)
);

create index index_comment_user_id
    on comments (user_id);

create index index_post_id
    on comments (post_id);

create table post_likes
(
    user_id int not null,
    post_id int not null,
    primary key (user_id, post_id),
    constraint post_likes_fk_post_id
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint post_likes_fk_user_id
        foreign key (user_id) references users (id)
            on delete cascade
);

create index index_postLikes_post_id
    on post_likes (post_id);

create table post_tags
(
    post_id int not null,
    tag_id  int not null,
    primary key (post_id, tag_id),
    constraint post_tags_fk_post_id
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint post_tags_fk_tag_id
        foreign key (tag_id) references tags (id)
            on delete cascade
);

create index index_created_at
    on posts (created_at);

create index index_user_id
    on posts (user_id);

