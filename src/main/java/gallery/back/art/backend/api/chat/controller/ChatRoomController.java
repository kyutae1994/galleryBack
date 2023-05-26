package gallery.back.art.backend.api.chat.controller;

import gallery.back.art.backend.api.chat.dto.ChatRoom;
import gallery.back.art.backend.api.chat.repository.ChatRepository;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatRepository chatRepository;

    // 채팅 리스트 화면
    // /chat 로 요청이 들어오면 전체 채팅룸 리스트를 담아서 return
    @GetMapping("/chat")
    public ResponseEntity goChatRoom() {

        List<ChatRoom> chatRoomList = chatRepository.findAllRoom();
        log.info("SHOW ALL ChatList {}", chatRepository.findAllRoom());
        return ResponseEntity.ok(BaseResponseDto.of(chatRoomList));
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 /chat 로 return
    @PostMapping("/chat/createroom")
    public ResponseEntity createRoom(@RequestParam String name, RedirectAttributes rttr) {
        ChatRoom room = chatRepository.createChatRoom(name);
        log.info("CREATE Chat Room {}", room);
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chat";
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    @GetMapping("/chat/room")
    public ResponseEntity roomDetail(Model model, String roomId){

        log.info("roomId {}", roomId);
        model.addAttribute("room", chatRepository.findRoomById(roomId));
        return "chatroom";
    }
}
