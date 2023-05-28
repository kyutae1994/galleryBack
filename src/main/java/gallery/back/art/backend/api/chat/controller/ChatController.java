package gallery.back.art.backend.api.chat.controller;

import gallery.back.art.backend.api.chat.dto.Chat;
import gallery.back.art.backend.api.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    // 채팅 리스트 화면
    // /chat 로 요청이 들어오면 전체 채팅룸 리스트를 담아서 return
    @MessageMapping("/receive")
    @SendTo("/send")
    public Chat chattingHandler(Chat chat) {
        return chatService.chattingHandler(chat);
    }
}
