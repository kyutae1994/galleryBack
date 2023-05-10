package gallery.back.art.backend.api.account.repository;

import gallery.back.art.backend.api.account.entity.Authority;
import gallery.back.art.backend.common.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findIDByRole(Role role);
}
