-- DROP SCHEMA public;

--CREATE SCHEMA public AUTHORIZATION pg_database_owner;

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
	NO CYCLE;-- public.genres definition

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


-- public.books definition

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
	CONSTRAINT books_pkey PRIMARY KEY (id),
	CONSTRAINT books_uploaded_by_fkey FOREIGN KEY (uploaded_by) REFERENCES public.users(id)
);


-- public.books_to_genres definition

-- Drop table

-- DROP TABLE public.books_to_genres;

CREATE TABLE public.books_to_genres (
	id serial4 NOT NULL,
	book_id int4 NULL,
	genre_id int4 NULL,
	CONSTRAINT books_to_genres_book_id_genre_id_key UNIQUE (book_id, genre_id),
	CONSTRAINT books_to_genres_pkey PRIMARY KEY (id),
	CONSTRAINT books_to_genres_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE CASCADE,
	CONSTRAINT books_to_genres_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES public.genres(id) ON DELETE CASCADE
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
	CONSTRAINT collections_pkey PRIMARY KEY (id),
	CONSTRAINT collections_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);


-- public.bookshelves_to_books definition

-- Drop table

-- DROP TABLE public.bookshelves_to_books;

CREATE TABLE public.bookshelves_to_books (
	id int4 DEFAULT nextval('collections_to_books_id_seq'::regclass) NOT NULL,
	bookshelf_id int4 NULL,
	book_id int4 NULL,
	CONSTRAINT collections_to_books_collection_id_book_id_key UNIQUE (bookshelf_id, book_id),
	CONSTRAINT collections_to_books_pkey PRIMARY KEY (id),
	CONSTRAINT collections_to_books_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE CASCADE,
	CONSTRAINT collections_to_books_collection_id_fkey FOREIGN KEY (bookshelf_id) REFERENCES public.bookshelves(id) ON DELETE CASCADE
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
	CONSTRAINT comments_pkey PRIMARY KEY (id),
	CONSTRAINT comments_parent_comment_id_fkey FOREIGN KEY (parent_comment_id) REFERENCES public."comments"(id),
	CONSTRAINT comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);


-- public.followers definition

-- Drop table

-- DROP TABLE public.followers;

CREATE TABLE public.followers (
	id serial4 NOT NULL,
	user_id int4 NULL,
	follower_id int4 NULL,
	CONSTRAINT followers_pkey PRIMARY KEY (id),
	CONSTRAINT followers_user_id_follower_id_key UNIQUE (user_id, follower_id),
	CONSTRAINT followers_follower_id_fkey FOREIGN KEY (follower_id) REFERENCES public.users(id) ON DELETE CASCADE,
	CONSTRAINT followers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);
