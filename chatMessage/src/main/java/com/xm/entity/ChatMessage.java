package com.xm.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("chat_message")
@Builder
public class ChatMessage {
    @Id
    private String id;
    private String sender;      // 发送人 username
    private String receiver;    // 接收人 username（群聊为空）
    private String content;
    private LocalDateTime time = LocalDateTime.now();
}