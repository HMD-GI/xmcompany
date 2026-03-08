package com.xm.controller;

import com.xm.assistant.Assistant;
import com.xm.entity.ChatMessages;
import com.xm.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "AI智能助手", description = "基于大语言模型的智能对话和问答功能")
@RestController
@RequestMapping("/xm/ai")
public class XMAiController {

    @Autowired
    private Assistant assistant;

    @Autowired
    private ChatService chatService;
    
    @Operation(summary = "智能对话", description = "与AI助手进行实时对话，支持流式响应")
    @PostMapping(value = "/chat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatMessages chatMessages) {
        chatService.chat(chatMessages);
        return assistant.chat(chatMessages.getMessageId(), chatMessages.getContent());
    }

}
