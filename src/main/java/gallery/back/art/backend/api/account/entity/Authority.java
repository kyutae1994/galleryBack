package gallery.back.art.backend.api.account.entity;

import gallery.back.art.backend.common.auth.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL)
    private List<MemberAuthorityMapping> memberAuthorityMappings = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    public String getRoleKey() {
        return this.role.getKey();
    }

    @Builder
    public Authority(Long id, List<MemberAuthorityMapping> memberAuthorityMappings, Role role) {
        this.id = id;
        this.memberAuthorityMappings = memberAuthorityMappings;
        this.role = role;
    }
}
