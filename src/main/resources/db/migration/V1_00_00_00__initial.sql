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

