package gallery.back.art.backend.api.chat.dto;

import gallery.back.art.backend.api.account.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "message")
    private String message;

    @Column(name = "send_at")
    private Timestamp sendAt;

    @Column(name = "is_request")
    private Boolean isRequest;
}
