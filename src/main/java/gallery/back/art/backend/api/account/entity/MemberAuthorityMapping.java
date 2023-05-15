package gallery.back.art.backend.api.account.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "member_authority")
@NoArgsConstructor
public class MemberAuthorityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_authority_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    @Builder
    public MemberAuthorityMapping(Long id, Member member, Authority authority) {
        this.id = id;
        this.member = member;
        this.authority = authority;
    }
}
