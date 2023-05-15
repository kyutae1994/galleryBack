package gallery.back.art.backend.api.account.repository;

import gallery.back.art.backend.api.account.entity.MemberAuthorityMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountAuthorityRepository extends JpaRepository<MemberAuthorityMapping, Long> {
    MemberAuthorityMapping findAuthorityByMember(Long member_id);
}
