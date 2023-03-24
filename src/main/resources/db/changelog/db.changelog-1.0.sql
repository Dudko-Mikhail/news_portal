--liquibase formatted sql

--changeset dudkomikhail:1
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(40) UNIQUE NOT NULL,
    password VARCHAR(78) NOT NULL,
    name VARCHAR(20),
    surname VARCHAR(20),
    parent_name VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_edit_date TIMESTAMP NOT NULL,
    is_deleted BOOLEAN NOT NULL
);
--rollback DROP TABLE users

--changeset dudkomikhail:2
CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    text VARCHAR(2000) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_edit_date TIMESTAMP NOT NULL,
    inserted_by_id BIGINT REFERENCES users(id) NOT NULL,
    updated_by_id BIGINT REFERENCES users(id) NOT NULL
);
--rollback DROP TABLE news

--changeset dudkomikhail:3
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    text varchar(300) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_edit_date TIMESTAMP NOT NULL,
    inserted_by_id BIGINT REFERENCES users(id) NOT NULL,
    news_id BIGINT REFERENCES news(id) ON DELETE CASCADE NOT NULL
);
--rollback DROP TABLE comments
