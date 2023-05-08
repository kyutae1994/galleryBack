package gallery.back.art.backend.api.account.entity;

import gallery.back.art.backend.common.auth.Role;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @OneToMany(mappedBy = "authority")
    private List<Member_Authority_Mapping> memberAuthorityMappings = new ArrayList<>();

    @Enumerated
    private Role role;
}
