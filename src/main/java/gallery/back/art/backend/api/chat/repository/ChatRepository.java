package gallery.back.art.backend.api.chat.repository;

import gallery.back.art.backend.api.chat.dto.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
