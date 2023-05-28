package gallery.back.art.backend.api.account.entity;

import gallery.back.art.backend.api.chat.dto.Chat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAuthorityMapping> memberAuthorityMappings = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Chat chat;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String createDate;

    @Builder
    public Member(Long id, List<MemberAuthorityMapping> memberAuthorityMappings, String email, String password, String name, String birthDate, String createDate) {
        this.id = id;
        this.memberAuthorityMappings = memberAuthorityMappings;
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.createDate = createDate;
    }
}
