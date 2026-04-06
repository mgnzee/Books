create table public.posts(
                             id serial4 not null,
                             user_id int4,
                             title varchar(150) not null,
                             "text" text,
                             upvotes int4 default 0 not null,
                             downvotes int4 default 0 not null,
                             comment_count int4 default 0 not null,
                             created_at timestamptz default current_timestamp,
                             updated_at timestamptz default current_timestamp,
                             constraint posts_pkey primary key(id),
                             constraint fk_posts_user foreign key (user_id) references users(id) on delete set null
);
CREATE INDEX idx_posts_user_id ON public.posts(user_id);