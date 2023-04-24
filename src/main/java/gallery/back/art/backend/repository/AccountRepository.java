package gallery.back.art.backend.repository;

import gallery.back.art.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Member, Integer> {
    Member findByEmailAndPassword(String email, String password);
    Member findByEmail(String email);
}
