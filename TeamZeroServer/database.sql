CREATE TABLE users(
    user_id serial PRIMARY KEY,
    username VARCHAR (50) UNIQUE NOT NULL,
    email VARCHAR (355) UNIQUE NOT NULL,
    password VARCHAR (50) NOT NULL
);

CREATE TABLE user_info(
    user_id integer PRIMARY KEY,
    avatar BYTEA,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE groups(
    group_id serial PRIMARY KEY,
    group_name VARCHAR (50) UNIQUE NOT NULL,
    number_of_members INTEGER NOT NULL,
    group_avatar BYTEA
);

CREATE TABLE user_groups(
    user_id integer,
    group_id integer,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (group_id) REFERENCES groups (group_id)
);

CREATE TABLE chats(
    chat_id serial PRIMARY KEY,
    user1 integer NOT NULL,
    user2 integer NOT NULL,
    FOREIGN KEY (user1) REFERENCES users (user_id),
    FOREIGN KEY (user2) REFERENCES users (user_id)
);

CREATE TABLE messages(
    mesasge_id serial PRIMARY KEY,
    chat_id integer NOT NULL,
    sender_id integer NOT NULL,
    recipient_id integer NOT NULL,
    timesent TIMESTAMP NOT NULL,
    group_flag BOOLEAN NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats (chat_id),
    FOREIGN KEY (sender_id) REFERENCES users (user_id),
    FOREIGN KEY (recipient_id) REFERENCES users (user_id)
);