package gallery.back.art.backend.api.account.repository;

import gallery.back.art.backend.api.account.entity.Member_Authority_Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Account_Authority_Repository extends JpaRepository<Member_Authority_Mapping, Long> {
    Member_Authority_Mapping findAuthorityByMember(Long member_id);
}
