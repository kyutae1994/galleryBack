package gallery.back.art.backend.api.account.repository;

import gallery.back.art.backend.api.account.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Member, Long> {
    Member findByEmailAndPassword(String email, String password);
    Member findByEmail(String email);
}
