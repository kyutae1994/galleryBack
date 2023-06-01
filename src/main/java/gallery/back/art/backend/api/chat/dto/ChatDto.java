package gallery.back.art.backend.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {

    // 유저의 이름을 저장하기 위한 변수
    private String userName;

    // 메시지의 내용을 저장하기 위한 변수
    private String message;

    // TODO - 후에 디비 연동시키면 작업해보기
    // 메시지 보낸 시간
//    private String sendTime;
}
