package com.xm.controller;

import com.xm.dto.ChatMessageDTO;
import com.xm.entity.ChatMessage;
import com.xm.mongoRepository.ChatMessageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "消息管理", description = "实现聊天模块")
public class ChatController {

    private final ChatMessageRepository repository;
    private final SimpMessagingTemplate messaging;   // 用来点对点/广播

    /**
     * 客户端发送：/app/chat_send
     *  payload: {"receiver":"bob","content":"你好"}
     */
    @Operation(summary = "发送消息", description = "发送消息")
    @MessageMapping("/chat_send")
    public void send(ChatMessageDTO dto, Principal principal) {
        String sender = principal.getName();   // 握手时塞的 username
        ChatMessage msg = ChatMessage.builder()
                .sender(sender)
                .receiver(dto.getReceiver())
                .content(dto.getContent())
                .build();
        repository.save(msg);                  // 落库

        if (StringUtils.hasLength(dto.getReceiver())) {
            // 点对点 /user/bob/queue/chat
            messaging.convertAndSendToUser(dto.getReceiver(), "/queue/chat", msg);
            // 同时给发送人也回显一条
            messaging.convertAndSendToUser(sender, "/queue/chat", msg);
        } else {
            // 群聊
            messaging.convertAndSend("/topic/public", msg);
        }
    }

    /**
     * REST 接口：拉历史
     */
    @Operation(summary = "历史消息", description = "查看历史消息")
    @GetMapping("/api/history/{friend}")
    public List<ChatMessage> history(@PathVariable String friend,
                                     Principal principal) {
        String me = principal.getName();
        return repository.findBySenderAndReceiverOrReceiverAndSenderOrderByTime(
                me, friend, me, friend);
    }
}