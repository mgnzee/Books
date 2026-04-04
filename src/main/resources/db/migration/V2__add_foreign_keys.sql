-- This migration adds foreign keys to entities

-- bookshelves to books
alter table bookshelves_to_books drop constraint if exists fk_btb_bookshelf;
alter table bookshelves_to_books drop constraint if exists fk_btb_book;

alter table bookshelves_to_books add constraint fk_btb_bookshelf foreign key (bookshelf_id) references bookshelves (id) on delete cascade;

alter table bookshelves_to_books add constraint fk_btb_book foreign key (book_id) references books (id) on delete cascade;

-- bookshelves
alter table bookshelves drop constraint if exists fk_bookshelf_user;

alter table bookshelves add constraint fk_bookshelf_user foreign key (user_id) references users (id) on delete set null;

-- books to genres
alter table books_to_genres drop constraint if exists fk_btg_book;
alter table books_to_genres drop constraint if exists fk_btg_genre;

alter table books_to_genres add constraint fk_btg_book foreign key (book_id) references books (id) on delete cascade;
alter table books_to_genres add constraint fk_btg_genre foreign key (genre_id) references genres (id) on delete cascade;

-- comments
alter table "comments" drop constraint if exists fk_user;

alter table "comments" add constraint fk_user foreign key (user_id) references users (id) on delete set null;

-- followers
alter table followers drop constraint if exists fk_user;
alter table followers drop constraint if exists fk_follower;

alter table followers add constraint fk_user foreign key (user_id) references users (id) on delete cascade;
alter table followers add constraint fk_follower foreign key (follower_id) references users (id) on delete cascade;