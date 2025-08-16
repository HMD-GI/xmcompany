package com.xm.service.impl;

import com.xm.entity.ChatMessages;
import com.xm.service.ChatService;
import com.xm.utils.UserContext;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    @Override
    public void chat(ChatMessages chatMessages) {
        chatMessages.setMessageId(UserContext.getCurrentEmployeeId());
    }
}
