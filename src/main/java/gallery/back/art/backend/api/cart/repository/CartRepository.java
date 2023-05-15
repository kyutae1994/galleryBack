package gallery.back.art.backend.api.cart.repository;

import gallery.back.art.backend.api.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    List<Cart> findByMemberId(Long memberId);

    Cart findByMemberIdAndItemId(Long memberId, Long itemId);

    void deleteByMemberId(Long memberId);
}
