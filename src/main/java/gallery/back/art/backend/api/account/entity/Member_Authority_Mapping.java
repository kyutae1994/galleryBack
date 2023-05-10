package gallery.back.art.backend.api.account.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "Member_Authority")
@NoArgsConstructor
public class Member_Authority_Mapping {

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
    public Member_Authority_Mapping(Long id, Member member, Authority authority) {
        this.id = id;
        this.member = member;
        this.authority = authority;
    }
}
