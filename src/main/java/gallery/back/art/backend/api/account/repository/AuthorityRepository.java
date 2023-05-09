package gallery.back.art.backend.api.account.repository;

import gallery.back.art.backend.api.account.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
