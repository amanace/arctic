package com.acrtic.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acrtic.chat.model.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity,Long> {
     List<MessageEntity> findBySenderAndReceiverOrReceiverAndSender(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
}
