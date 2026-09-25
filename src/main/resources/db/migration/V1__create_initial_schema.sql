CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Create missing schemas
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS room;
CREATE SCHEMA IF NOT EXISTS message;

-- 2. Auth Tables
CREATE TABLE auth.user (
    user_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url    VARCHAR(500),
    status        VARCHAR(20)  NOT NULL DEFAULT 'OFFLINE',
    last_seen     TIMESTAMP WITH TIME ZONE,
    insert_dt     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- 3. Room Tables
CREATE TABLE room.room (
    room_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100),
    type       VARCHAR(10) NOT NULL CHECK (type IN ('DM', 'GROUP')),
    created_by UUID NOT NULL REFERENCES auth.user(user_id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE room.room_member (
    room_member_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id        UUID NOT NULL REFERENCES room.room(room_id) ON DELETE CASCADE,
    user_id        UUID NOT NULL REFERENCES auth.user(user_id) ON DELETE CASCADE,
    role           VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    last_read_at   TIMESTAMP WITH TIME ZONE,
    UNIQUE (room_id, user_id)
);

-- 4. Message Tables
CREATE TABLE message.message (
    message_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id      UUID NOT NULL REFERENCES room.room(room_id) ON DELETE CASCADE,
    sender_id    UUID NOT NULL REFERENCES auth.user(user_id),
    content      TEXT NOT NULL,
    content_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    status       VARCHAR(20) NOT NULL DEFAULT 'SENT',
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    edited_at    TIMESTAMP WITH TIME ZONE
);

CREATE TABLE message.message_read (
    message_read_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id      UUID NOT NULL REFERENCES message.message(message_id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES auth.user(user_id) ON DELETE CASCADE,
    read_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (message_id, user_id)
);

CREATE TABLE message.attachment (
    attachment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id    UUID NOT NULL REFERENCES message.message(message_id) ON DELETE CASCADE,
    file_url      VARCHAR(500) NOT NULL,
    file_type     VARCHAR(50),
    file_size     INTEGER
);

-- 5. Indexes (Corrected table names)
CREATE INDEX idx_messages_room_id   ON message.message(room_id, created_at DESC);
CREATE INDEX idx_room_members_user  ON room.room_member(user_id);
CREATE INDEX idx_message_reads_user ON message.message_read(user_id);