package com.xm.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String receiver; // 为空表示群聊
    private String content;
}