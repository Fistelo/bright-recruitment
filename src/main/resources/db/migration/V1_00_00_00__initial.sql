CREATE TABLE books
(
    id                   SERIAL PRIMARY KEY,
    title                varchar not null,
    author               varchar,
    isbn                 varchar,
    number_of_pages      integer,
    rating               integer
);

CREATE TABLE comments
(
    id            SERIAL PRIMARY KEY,
    content              varchar not null,
    created_at            timestamp,
    book_id              integer,

    FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE INDEX comment_time_desc_idx ON comments (created_at DESC NULLS LAST);
CREATE INDEX comments_book_foreign_key_idx ON comments (book_id);

