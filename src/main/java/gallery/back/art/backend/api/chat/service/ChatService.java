package gallery.back.art.backend.api.chat.service;

import gallery.back.art.backend.api.account.entity.Member;
import gallery.back.art.backend.api.account.repository.AccountRepository;
import gallery.back.art.backend.api.chat.dto.Chat;
import gallery.back.art.backend.api.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AccountRepository accountRepository;
    private final ChatRepository chatRepository;

    public Chat chattingHandler(Chat chat) {
        Member member = accountRepository.findById(chat.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다."));

        chat.setMember(member);

        return chatRepository.save(chat);
    }
}
