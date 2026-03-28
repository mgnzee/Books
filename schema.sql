DROP SCHEMA public;

CREATE SCHEMA public AUTHORIZATION pg_database_owner;

COMMENT ON SCHEMA public IS 'standard public schema';

-- DROP SEQUENCE public.books_id_seq;

CREATE SEQUENCE public.books_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.books_to_genres_id_seq;

CREATE SEQUENCE public.books_to_genres_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.collections_id_seq;

CREATE SEQUENCE public.collections_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.collections_to_books_id_seq;

CREATE SEQUENCE public.collections_to_books_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.comments_id_seq;

CREATE SEQUENCE public.comments_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.followers_id_seq;

CREATE SEQUENCE public.followers_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.genres_id_seq;

CREATE SEQUENCE public.genres_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;
-- DROP SEQUENCE public.users_id_seq;

CREATE SEQUENCE public.users_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;-- public.books definition

-- Drop table

-- DROP TABLE public.books;

CREATE TABLE public.books (
                              id serial4 NOT NULL,
                              title varchar(100) NOT NULL,
                              description text NULL,
                              "language" varchar(50) NULL,
                              rating numeric(3, 2) NULL,
                              author varchar(255) NULL,
                              uploaded_by int4 NULL,
                              file_url text NULL,
                              cover_image text NULL,
                              download_count int4 DEFAULT 0 NULL,
                              page_count int4 DEFAULT 0 NULL,
                              created_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                              updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                              comment_count int4 DEFAULT 0 NOT NULL,
                              CONSTRAINT books_pkey PRIMARY KEY (id)
);


-- public.books_to_genres definition

-- Drop table

-- DROP TABLE public.books_to_genres;

CREATE TABLE public.books_to_genres (
                                        id serial4 NOT NULL,
                                        book_id int4 NULL,
                                        genre_id int4 NULL,
                                        CONSTRAINT books_to_genres_book_id_genre_id_key UNIQUE (book_id, genre_id),
                                        CONSTRAINT books_to_genres_pkey PRIMARY KEY (id)
);


-- public.bookshelves definition

-- Drop table

-- DROP TABLE public.bookshelves;

CREATE TABLE public.bookshelves (
                                    id int4 DEFAULT nextval('collections_id_seq'::regclass) NOT NULL,
                                    user_id int4 NULL,
                                    title varchar(100) NOT NULL,
                                    description text NULL,
                                    cover text NULL,
                                    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                                    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                                    CONSTRAINT collections_pkey PRIMARY KEY (id)
);


-- public.bookshelves_to_books definition

-- Drop table

-- DROP TABLE public.bookshelves_to_books;

CREATE TABLE public.bookshelves_to_books (
                                             id int4 DEFAULT nextval('collections_to_books_id_seq'::regclass) NOT NULL,
                                             bookshelf_id int4 NULL,
                                             book_id int4 NULL,
                                             CONSTRAINT collections_to_books_collection_id_book_id_key UNIQUE (bookshelf_id, book_id),
                                             CONSTRAINT collections_to_books_pkey PRIMARY KEY (id)
);


-- public."comments" definition

-- Drop table

-- DROP TABLE public."comments";

CREATE TABLE public."comments" (
                                   id serial4 NOT NULL,
                                   user_id int4 NULL,
                                   target_type varchar(20) NOT NULL,
                                   target_id int4 NOT NULL,
                                   parent_comment_id int4 NULL,
                                   "text" text NOT NULL,
                                   upvotes int4 DEFAULT 0 NULL,
                                   downvotes int4 DEFAULT 0 NULL,
                                   created_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                                   updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                                   is_deleted bool DEFAULT false NULL,
                                   replies_count int4 DEFAULT 0 NOT NULL,
                                   CONSTRAINT comments_pkey PRIMARY KEY (id)
);


-- public.followers definition

-- Drop table

-- DROP TABLE public.followers;

CREATE TABLE public.followers (
                                  id serial4 NOT NULL,
                                  user_id int4 NULL,
                                  follower_id int4 NULL,
                                  CONSTRAINT followers_pkey PRIMARY KEY (id),
                                  CONSTRAINT followers_user_id_follower_id_key UNIQUE (user_id, follower_id)
);


-- public.genres definition

-- Drop table

-- DROP TABLE public.genres;

CREATE TABLE public.genres (
                               id serial4 NOT NULL,
                               title varchar(50) NOT NULL,
                               CONSTRAINT genres_pkey PRIMARY KEY (id),
                               CONSTRAINT genres_title_key UNIQUE (title)
);


-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
                              id serial4 NOT NULL,
                              "name" varchar(25) NOT NULL,
                              email varchar(100) NOT NULL,
                              profile_pic_url text NULL,
                              created_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                              updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
                              "password" varchar(255) NULL,
                              is_deleted bool DEFAULT false NOT NULL,
                              is_disabled bool DEFAULT false NOT NULL,
                              CONSTRAINT users_email_key UNIQUE (email),
                              CONSTRAINT users_email_key1 UNIQUE (email),
                              CONSTRAINT users_pkey PRIMARY KEY (id)
);