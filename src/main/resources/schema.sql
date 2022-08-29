CREATE TABLE IF NOT EXISTS users (
     user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     user_name VARCHAR(255) NOT NULL,
     email VARCHAR(512) NOT NULL,
     CONSTRAINT pk_user PRIMARY KEY (user_id),
     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
     item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     owner_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
     item_name VARCHAR(255) NOT NULL,
     description VARCHAR(1024),
     available BOOLEAN NOT NULL,
     CONSTRAINT pk_item PRIMARY KEY (item_id)
)