package com.acrtic.chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String sender;
    public String receiver;
    public String content;

    public LocalDateTime timestamp = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    public Status status = Status.SENT;

    public enum Status {
        SENT,
        DELIVERED,
        SEEN
    }
}
