package gallery.back.art.backend.api.chat.controller;

import gallery.back.art.backend.api.chat.dto.ChatDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/ws")
public class ChatController {

    // /receive를 메시지를 받을 endpoint로 설정합니다.
    @MessageMapping("/message")
    // /send로 메시지를 반환합니다.
    @SendTo("/send/messages")
    // SocketHandler는 1) /receive에서 메시지를 받고, /send로 메시지를 보내줍니다.
    // 정의한 SocketVO를 1) 인자값, 2) 반환값으로 사용합니다.
    public ResponseEntity SocketHandler(ChatDto socketVO) throws InterruptedException {
        // vo에서 getter로 userName을 가져옵니다.
        String userName = socketVO.getUserName();
        // vo에서 setter로 content를 가져옵니다.
        String message = socketVO.getMessage();

        // 생성자로 반환값을 생성합니다.
        ChatDto result = new ChatDto(userName, message);
        // 반환
//        return ResponseEntity.ok(BaseResponseDto.of(result));
        Thread.sleep(1000);
        return ResponseEntity.ok(result);
    }
}
