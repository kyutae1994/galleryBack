package gallery.back.art.backend.api.chat.controller;

import gallery.back.art.backend.api.chat.dto.ChatDto;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ws")
public class ChatController {

    // 클라이언트가 /message 로 보내면 서비스단에서 @MessageMapping 선언한 곳에서 받음.
    @MessageMapping("/message")
    // 서비스단에서 클라이언트로 메시지 보냄.
    @SendTo("/topic/messages")
    public ResponseEntity SocketHandler(ChatDto socketVO) throws InterruptedException {
        String userName = socketVO.getUserName();
        String message = socketVO.getMessage();

        ChatDto result = new ChatDto(userName, message);
        Thread.sleep(1000);

        return ResponseEntity.ok(BaseResponseDto.of(result));
    }
}
