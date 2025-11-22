package com.xm.mongoRepository;

import com.xm.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    // 查询两人聊天记录
    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSenderOrderByTime(
            String s1, String r1, String s2, String r2);
}