package com.ajinkya.chatme.entity;

import java.sql.Timestamp;

public record RoomWithLastRead(Room room, Timestamp lastReadAt) {}
